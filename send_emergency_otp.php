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
    echo json_encode(["success"=>false,"message"=>"Invalid request"]);
    exit;
}

$public_id = trim($_POST['public_id'] ?? "");

if($public_id==""){
    echo json_encode(["success"=>false,"message"=>"Missing public_id"]);
    exit;
}

/* Get emergency email */
$stmt = $con->prepare("SELECT emergency_email, emergency_name FROM user_details WHERE public_id=?");
$stmt->bind_param("s",$public_id);
$stmt->execute();
$res = $stmt->get_result();

if($res->num_rows==0){
    echo json_encode(["success"=>false,"message"=>"User not found"]);
    exit;
}

$row = $res->fetch_assoc();
$email = $row['emergency_email'];
$name  = $row['emergency_name'];

if(empty($email)){
    echo json_encode(["success"=>false,"message"=>"No emergency email found"]);
    exit;
}

/* Generate OTP */
$otp = strval(rand(100000,999999));

/* Save OTP */
$stmt2 = $con->prepare("
    INSERT INTO emergency_access (public_id, otp, expires_at)
    VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 5 MINUTE))
");

$stmt2->bind_param("ss",$public_id,$otp);

if(!$stmt2->execute()){
    echo json_encode(["success"=>false,"message"=>"DB error: OTP not saved"]);
    exit;
}

$stmt2->close();

/* Send Email */
$mail = new PHPMailer(true);

try{

$mail->isSMTP();
$mail->Host       = "smtp.gmail.com";
$mail->SMTPAuth   = true;
$mail->Username   = SMTP_EMAIL;
$mail->Password   = SMTP_PASS;

$mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
$mail->Port       = 587;

$mail->setFrom(SMTP_EMAIL,"MedVault Emergency");

$mail->addAddress($email,$name);

$mail->isHTML(true);

$mail->Subject = "Emergency Access OTP - MedVault";

$mail->Body = "
<h2>MedVault Emergency OTP</h2>

<p>Emergency access requested for a patient.</p>

<p>Your OTP:</p>

<h1>$otp</h1>

<p>This OTP expires in 5 minutes.</p>
";

$mail->send();

echo json_encode([
"success"=>true,
"message"=>"OTP sent to emergency contact email"
]);

}catch(Exception $e){

echo json_encode([
"success"=>false,
"message"=>"Mail error: ".$mail->ErrorInfo
]);

}
?>