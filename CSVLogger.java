import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;

public class CSVLogger{

static public void main(String[] args)
{
  if(args.length!=3)
  {
    System.out.println("Arguments: <connection string> <username> <password>");
    return;
  }
  PrintWriter writer=null;
  try{
    //STEP 2: Register JDBC driver
    Class.forName("com.mysql.jdbc.Driver");

    //STEP 3: Open a connection
    System.out.println("Connecting to database...");
    Connection connection = DriverManager.getConnection(args[0],args[1],args[2]);
    System.out.println("Connected");
    String query_sensor="select * from sensor_log;";
    String query_command="select * from command_log";
    writer=new PrintWriter(new BufferedWriter(new FileWriter("sensor.csv",true)));
    PreparedStatement pstmt=connection.prepareStatement(query_sensor);
    ResultSet result=pstmt.executeQuery();
    while(result.next())
    {
      writer.println(result.getInt("seqnum")+","+result.getInt("PsID")+","+result.getInt("level1")+","+result.getInt("level2")+","+result.getInt("level3")+","+result.getInt("level4")+","+result.getTimestamp("time"));
    }
    writer.close();
    writer=new PrintWriter(new BufferedWriter(new FileWriter("command.csv",true)));
    pstmt=connection.prepareStatement(query_command);
    result=pstmt.executeQuery();
    while(result.next())
    {
      writer.println(result.getInt("seqnum")+","+result.getInt("PsID")+","+result.getInt("pumpStatus")+","+result.getTimestamp("time"));
    }
    writer.close();
  }
  catch(SQLException se){
    //Handle errors for JDBC
    se.printStackTrace();
 }
 catch(ClassNotFoundException ce)
 {
   ce.printStackTrace();
 }
 catch(IOException ex)
 {
   ex.printStackTrace();
 }
 finally{
   writer.close();
 }
}
}
