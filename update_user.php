<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("connection.php");

$uid = $_POST['uid'];

$name = $_POST['name'];
$dob = $_POST['dob'];
$blood_group = $_POST['blood_group'];
$allergies = $_POST['allergies'];
$address = $_POST['address'];
$email = $_POST['email'];
$phone = $_POST['phone'];

$emergency_name = $_POST['emergency_name'];
$emergency_email = $_POST['emergency_email'];
$emergency_phone = $_POST['emergency_phone'];

$sql = "UPDATE user_details SET
name='$name',
dob='$dob',
blood_group='$blood_group',
allergies='$allergies',
address='$address',
email='$email',
phone='$phone',
emergency_name='$emergency_name',
emergency_email='$emergency_email',
emergency_phone='$emergency_phone'
WHERE id='$uid'";

if(mysqli_query($con,$sql)){

    echo json_encode([
        "success" => true,
        "message" => "Profile Updated"
    ]);

}else{

    echo json_encode([
        "success" => false,
        "error" => mysqli_error($con)
    ]);

}
?>