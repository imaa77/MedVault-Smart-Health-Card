<?php
include("connection.php");

$id = $_POST['id'];

$sql = "DELETE FROM medicine_reminders WHERE id='$id'";

if(mysqli_query($con,$sql)){
    echo json_encode(["success"=>true]);
}else{
    echo json_encode(["success"=>false]);
}
?>