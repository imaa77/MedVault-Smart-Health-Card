<?php
include("connection.php");

$id = $_POST['id'];

$stmt = $con->prepare("DELETE FROM reminders WHERE id=?");
$stmt->bind_param("i",$id);

if($stmt->execute()){
    echo json_encode([
        "success"=>true,
        "message"=>"Reminder deleted"
    ]);
}else{
    echo json_encode([
        "success"=>false,
        "message"=>"Error"
    ]);
}
?>