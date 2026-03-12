<?php
// Enable error reporting to find the cause of 500 error
error_reporting(E_ALL);
ini_set('display_errors', 1);

include("connection.php");

if(isset($_POST['id'])){
    $id = $_POST['id'];

    // REPLACE 'user_details' with your actual table name from the database
    $tableName = "medical_history"; // Change this if your table is named differently!

    // 1. Delete the record
    $sql = "DELETE FROM $tableName WHERE id='$id'";

    if(mysqli_query($con, $sql)){
        echo json_encode(array("success" => true, "message" => "Record deleted"));
    } else {
        // This message will show in Android if there's a database error
        echo json_encode(array("success" => false, "message" => "SQL Error: " . mysqli_error($con)));
    }
} else {
    echo json_encode(array("success" => false, "message" => "No ID received"));
}
?>