 <?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'phpmailer/PHPMailer-master/src/Exception.php';
require 'phpmailer/PHPMailer-master/src/PHPMailer.php';
require 'phpmailer/PHPMailer-master/src/SMTP.php';

require 'smtp_config.php'; // your SMTP_EMAIL + SMTP_PASS file

$mail = new PHPMailer(true);

try {
    $mail->isSMTP();
    $mail->Host = "smtp.gmail.com";
    $mail->SMTPAuth = true;
    $mail->Username = SMTP_EMAIL;
    $mail->Password = SMTP_PASS;
    $mail->SMTPSecure = "tls";
    $mail->Port = 587;

    $mail->setFrom(SMTP_EMAIL, "MedVault");
    $mail->addAddress("medvault2026@gmail.com");  // <-- change this

    $mail->isHTML(true);
    $mail->Subject = "MedVault Test Email";
    $mail->Body = "<h2>Hello!</h2><p>Your PHPMailer is working ✅</p>";

    $mail->send();
    echo "Email sent successfully ✅";
} catch (Exception $e) {
    echo "Email failed ❌ Error: {$mail->ErrorInfo}";
}
