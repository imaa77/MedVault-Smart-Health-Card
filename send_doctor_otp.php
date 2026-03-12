<?php
header("Content-Type: application/json");

include("connection.php");
include("mail_conf.php");

require 'phpmailer/PHPMailer-master/src/PHPMailer.php';
require 'phpmailer/PHPMailer-master/src/SMTP.php';
require 'phpmailer/PHPMailer-master/src/Exception.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

if ($_SERVER['REQUEST_METHOD'] != "POST") {
    echo json_encode(["success" => false, "message" => "Method not allowed. Use POST"]);
    exit;
}

$public_id = trim($_POST['public_id'] ?? "");

if ($public_id == "") {
    echo json_encode(["success" => false, "message" => "Missing public_id"]);
    exit;
}

/* 1) Get patient details */
$stmt = $con->prepare("SELECT name, email FROM user_details WHERE public_id=?");
$stmt->bind_param("s", $public_id);
$stmt->execute();
$res = $stmt->get_result();

if ($res->num_rows == 0) {
    echo json_encode(["success" => false, "message" => "Invalid QR"]);
    exit;
}

$user  = $res->fetch_assoc();
$name  = $user['name'];
$email = $user['email'];
$stmt->close();

/* 2) Create OTP */
$otp = strval(rand(100000, 999999));

/* 3) Save OTP in DB (IMPORTANT FIX: expiry handled by MySQL time) */
$stmt2 = $con->prepare("
    INSERT INTO doctor_access (public_id, otp, expires_at)
    VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 10 MINUTE))
");
$stmt2->bind_param("ss", $public_id, $otp);

if (!$stmt2->execute()) {
    echo json_encode(["success" => false, "message" => "DB Error: OTP not saved"]);
    exit;
}

$stmt2->close();

/* 4) Send email */
$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host       = "smtp.gmail.com";
    $mail->SMTPAuth   = true;
    $mail->Username   = SMTP_EMAIL;
    $mail->Password   = SMTP_PASS;

    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port       = 587;

    $mail->setFrom(SMTP_EMAIL, "MedVault");
    $mail->addAddress($email, $name);

    $mail->isHTML(true);
    $mail->Subject = "Doctor Access OTP - MedVault";
    $mail->Body = "
        <h2>MedVault Doctor OTP</h2>
        <p>Hello <b>$name</b>,</p>
        <p>Your OTP for doctor access is:</p>
        <h1>$otp</h1>
        <p>This OTP is valid for 10 minutes.</p>
    ";

    $mail->send();

    echo json_encode(["success" => true, "message" => "OTP sent to patient email"]);

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Mail failed: " . $mail->ErrorInfo]);
}
?>
