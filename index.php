<?php header('Refresh: 10'); ?>
<html>
<head>
<title>Monitor page</title>
<style>
body {background-color:lightgrey;}
h1   {color:blue;}
h3 {text-align: center;}
table,tr,td{
	align: center;
	text-align: center;
}
</style>
</head>
<body>
<h1 align="center"><br/> Welcome to the Smart Sewage Monitor<br/></h1>


<?php
$host="localhost"; 
$username="root"; 
$password=""; 
$db_name="smartsewage";
$tbl_name="pumping_station"; 
$conn = mysqli_connect($host, $username, $password,$db_name)or die("cannot connect");

$sqltp="select * from treatment_plant";
if($resulttp=mysqli_query($conn,$sqltp))
	;
else echo Error;
$rowtp=mysqli_fetch_assoc($resulttp);


$sql="select * from ".$tbl_name." order by PsID";
if ($result=mysqli_query($conn,$sql))
	;
else echo "Error";
?>

<br/>
<br/>
<?php
echo "<h3> Treatment plant status: ".$rowtp['status']."</h3>";
?>
<br/>
<br/>
<table border='1' style="border-collapse:collapse;" cellpadding="5" align="center">
<tr>
	<th width='5'>PS ID</th>
	<th width='6'>Water Level</th> 
	<th width="8">Capacity(ml)</th>
	<th width="10"> Min. Time to Empty</th> 
	<th width='10'> Priority </th>
	<th width="6">Status</th>
</tr>
<?php
while($row=mysqli_fetch_assoc($result))
{
	echo "<tr><td>".$row['PsID']."</td><td>".$row['level']."</td>";
	echo "<td>".$row['capacity']."</td><td>".$row['minTimeToEmpty']."</td><td>".$row['priority']."</td><td>".$row['status']."</td></tr>";

}

?>

</table>
<br/>

</body>
</html>