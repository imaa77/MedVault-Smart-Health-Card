<?php
include("connection.php");

$id = $_POST['id'];

$stmt = $con->prepare("UPDATE reminders SET status='completed' WHERE id=?");
$stmt->bind_param("i",$id);

if($stmt->execute()){
    echo json_encode([
        "success"=>true
    ]);
}else{
    echo json_encode([
        "success"=>false
    ]);
}
?>