<?php
include("mail_conf.php");

require 'phpmailer/PHPMailer-master/src/PHPMailer.php';
require 'phpmailer/PHPMailer-master/src/SMTP.php';
require 'phpmailer/PHPMailer-master/src/Exception.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host = 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_EMAIL;
    $mail->Password = SMTP_PASS;
    $mail->SMTPSecure = 'tls';
    $mail->Port = 587;

    $mail->setFrom(SMTP_EMAIL, "MedVault");
    $mail->addAddress("YOUR_PERSONAL_GMAIL@gmail.com");

    $mail->isHTML(true);
    $mail->Subject = "SMTP Test MedVault";
    $mail->Body = "<h2>Hello</h2><p>This is a test mail.</p>";

    $mail->send();
    echo "✅ Mail sent successfully!";
} catch (Exception $e) {
    echo "❌ Mail failed: " . $mail->ErrorInfo;
}
