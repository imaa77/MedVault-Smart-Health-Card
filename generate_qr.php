<?php
include("connection.php");

if (!isset($_GET['id'])) {
    die("Pass public_id like: generate_qr.php?id=MV1");
}

$public_id = $_GET['id'];

// Get user name
$sql = "SELECT name, public_id FROM user_details WHERE public_id = ?";
$stmt = $con->prepare($sql);
$stmt->bind_param("s", $public_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    die("User not found!");
}

$user = $result->fetch_assoc();

// IMPORTANT:
// localhost will not work on phone.
// So use laptop IP manually (you can change this anytime)
$laptop_ip = "192.168.1.136";


// QR should open public page
$link = "http://" . $laptop_ip . "/medvault/public.php?id=" . $public_id;

// QR Image using QRServer API (works 100%)
$qrImage = "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" . urlencode($link);
?>

<!DOCTYPE html>
<html>
<head>
    <title>Generate QR</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        body{
            font-family: Arial;
            padding:20px;
            background:#f3fff5;
            text-align:center;
        }
        .card{
            background:white;
            padding:20px;
            border-radius:12px;
            max-width:450px;
            margin:auto;
            box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
        }
        img{
            width:250px;
            margin:15px 0;
        }
        .link{
            word-break: break-all;
            background:#eee;
            padding:10px;
            border-radius:10px;
        }
    </style>
</head>
<body>

<div class="card">
    <h2>QR Code Generated</h2>

    <p><b>User:</b> <?= htmlspecialchars($user['name']) ?></p>
    <p><b>Public ID:</b> <?= htmlspecialchars($public_id) ?></p>

    <img src="<?= $qrImage ?>" alt="QR Code">

    <p><b>QR Link:</b></p>
    <div class="link"><?= htmlspecialchars($link) ?></div>

    <p style="margin-top:15px;">Scan this QR from phone to open public page.</p>
</div>

</body>
</html>
