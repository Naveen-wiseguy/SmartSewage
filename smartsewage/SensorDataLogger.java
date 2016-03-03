package smartsewage;
import java.sql.*;
import java.net.*;
import java.util.*;

/**
* A class for logging all received sensor information into a database
*/
public class SensorDataLogger implements SensorDataListener{
  /**
  * The username of the database to be connected to
  */
  private String username;
  /**
  * Tracking wherther the connection to databse has been successfully established
  */
  private boolean connected=false;
  /**
  * The password of the databse to be connected to
  */
  private String password;
  /**
  * The connection string to be used for connecting to the database
  */
  private String connectionString;
  /**
  * The databse connection object to add the data to the database
  */
  private Connection connection;

  /**
  * Creates the data logger provided the basic database parameters
  * @param conn The connection string to be used for the database
  * @param user The username
  * @param pwd The password
  */
  public SensorDataLogger(String conn,String user,String pwd)
  {
    connectionString=conn;
    username=user;
    password=pwd;
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      connection = DriverManager.getConnection(connectionString,username,password);
      connected=true;
      System.out.println("Created a sensor data logger");
      Publisher.getInstance().addSensorDataListener(this);
    }
    catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }
   catch(ClassNotFoundException ce)
   {
     ce.printStackTrace();
   }
  }


  public void sensorDataReceived(SensorData data,Socket sock)
  {
    if(!connected)
      return;
    try{
      String sql="insert into sensor_log(PsID,level1,level2,level3,level4,time) values(?,?,?,?,?,NOW())";
      PreparedStatement ps=connection.prepareStatement(sql);
      ps.setInt(1,data.getId());
      ps.setInt(2,(int)data.getInput(0));
      ps.setInt(3,(int)data.getInput(1));
      ps.setInt(4,(int)data.getInput(2));
      ps.setInt(5,(int)data.getInput(3));
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }
}
