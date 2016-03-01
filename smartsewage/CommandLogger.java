package smartsewage;
import java.sql.*;
import java.net.*;
import java.util.*;

/**
* A class for logging all received sensor information into a database
*/
public class CommandLogger implements RelayCommandListener{
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
  * Creates the command logger provided the basic database parameters
  * @param conn The connection string to be used for the database
  * @param user The username
  * @param pwd The password
  */
  public CommandLogger(String conn,String user,String pwd)
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
      System.out.println("Created a command logger");
      Publisher.getInstance().addRelayCommandListener(this);
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


  public void relayCommandReceived(RelayCommand command)
  {
    if(!connected)
      return;
    try{
      String sql="insert into command_log(PsID,pumpStatus,time) values(?,?,NOW())";
      PreparedStatement ps=connection.prepareStatement(sql);
      ps.setInt(1,command.getId());
      ps.setInt(2,command.getOutput(0));
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }
}
