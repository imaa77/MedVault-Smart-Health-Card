<?php
header("Content-Type: application/json");
require_once "connection.php";

// Validate required fields
if (
    empty($_POST['name']) ||
    empty($_POST['age']) ||
    empty($_POST['blood']) ||
    empty($_POST['allergies']) ||
    empty($_POST['address']) ||
    empty($_POST['email']) ||
    empty($_POST['phone']) ||
    empty($_POST['password'])
) {
    echo json_encode([
        "success" => false,
        "message" => "Missing required fields"
    ]);
    exit;
}

$name      = $_POST['name'];
$age       = $_POST['age'];
$blood     = $_POST['blood'];
$allergies = $_POST['allergies'] ?? '';
$address     = $_POST['address'];
$email     = $_POST['email'];
$phone     = $_POST['phone'];
$password  = $_POST['password'];

// Check if user already exists
$check = $con->prepare(
    "SELECT id FROM user_details WHERE phone = ? OR email = ?"
);
$check->bind_param("ss", $phone, $email);
$check->execute();
$check->store_result();

if ($check->num_rows > 0) {
    echo json_encode([
        "success" => false,
        "message" => "User already exists"
    ]);
    exit;
}
$check->close();

// Insert user
$sql = "INSERT INTO user_details (name, age, blood_group, allergies, address, email, phone, password, status)
    VALUES ('$name','$age','$blood','$allergies','$address','$email','$phone','$password', 'active')";

// echo $sql; // Debugging line to check the generated SQL query
// exit;
$result = mysqli_query($con, $sql);

if ($result === true) {
    echo json_encode([
        "success" => true,
        "message" => "Signup successful"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Signup failed"
    ]);
}

mysqli_close($con);