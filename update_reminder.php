<?php
include("connection.php");

$id = $_POST['id'];
$title = $_POST['title'];
$description = $_POST['description'];
$time = $_POST['time'];

$stmt = $con->prepare("UPDATE reminders SET title=?,description=?,time=? WHERE id=?");
$stmt->bind_param("sssi",$title,$description,$time,$id);

if($stmt->execute()){
    echo json_encode([
        "success"=>true,
        "message"=>"Reminder updated"
    ]);
}else{
    echo json_encode([
        "success"=>false
    ]);
}
?>