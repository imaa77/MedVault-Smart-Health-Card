<?php

include("connection.php");

if (!isset($_GET['id'])) {
    die("Invalid Request");
}

$public_id = $_GET['id'];

/* Get user id */

$sql = "SELECT id FROM user_details WHERE public_id=?";
$stmt = $con->prepare($sql);
$stmt->bind_param("s", $public_id);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) {
    echo "User not found";
    exit;
}

$user = $res->fetch_assoc();
$uid = $user['id'];


/* Fetch medical history */

$sql = "SELECT * FROM medical_history WHERE uid=? AND status='active' ORDER BY date_time DESC";
$stmt = $con->prepare($sql);
$stmt->bind_param("i", $uid);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) {
    echo "<p>No medical history found</p>";
    exit;
}


while ($row = $res->fetch_assoc()) {

    echo '<div class="box">';

    echo "<b>Title:</b> " . htmlspecialchars($row['title']) . "<br>";
    echo "<b>Description:</b> " . htmlspecialchars($row['notes']) . "<br>";
    echo "<b>Date:</b> " . $row['date_time'] . "<br>";

    /* File display */

    if (!empty($row['file_name'])) {

        $file_path = htmlspecialchars($row['file_name']);

        echo "<br>";

        /* Check file exists */

        if (file_exists(__DIR__ . "/" . $file_path)) {

            echo "<a href='" . $file_path . "' target='_blank'>View File</a>";

            $ext = strtolower(pathinfo($file_path, PATHINFO_EXTENSION));

            /* Image preview */

            if (in_array($ext, ["jpg","jpeg","png","gif"])) {

                echo "<br><br>";
                echo "<img src='" . $file_path . "' width='150' style='border-radius:6px;'>";

            }

            /* PDF preview */

            if ($ext == "pdf") {

                echo "<br><br>";
                echo "<a href='" . $file_path . "' target='_blank'>📄 Open PDF</a>";

            }

        } else {

            echo "<p style='color:red;'>File not found on server</p>";

        }

    }

    echo '</div>';

}

?>