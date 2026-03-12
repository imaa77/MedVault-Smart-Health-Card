<?php

header("Content-Type: application/json");

include("connection.php");

if ($_SERVER['REQUEST_METHOD'] != "POST") {
    echo json_encode(["success"=>false,"message"=>"Invalid request"]);
    exit;
}

$public_id = trim($_POST['public_id'] ?? "");
$otp       = trim($_POST['otp'] ?? "");

if($public_id=="" || $otp==""){
    echo json_encode(["success"=>false,"message"=>"Missing data"]);
    exit;
}

/* Verify OTP */

$stmt = $con->prepare("
SELECT * FROM emergency_access
WHERE public_id=?
AND otp=?
AND expires_at >= NOW()
ORDER BY id DESC
LIMIT 1
");

$stmt->bind_param("ss",$public_id,$otp);

$stmt->execute();

$res = $stmt->get_result();

if($res->num_rows>0){

echo json_encode([
"success"=>true,
"message"=>"Emergency access granted"
]);

}else{

echo json_encode([
"success"=>false,
"message"=>"Invalid or expired OTP"
]);

}

$stmt->close();
?>