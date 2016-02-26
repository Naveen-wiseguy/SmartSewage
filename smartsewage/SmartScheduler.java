package smartsewage;

import java.util.*;
import java.sql.*;
import java.net.*;

public class SmartScheduler extends Scheduler implements Runnable{
  private TreatmentPlantData tp;
  private ArrayList<PumpingStationData> ps;
  private ArrayList<TreatmentPlantInput> ip;
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
  * The interval at which scheduling has to take place
  */
  private long schedInterval;

  private Time minRunTime;

  public SmartScheduler(int TpID,String conn,String user,String pwd,long schedInterval)
  {
    connectionString=conn;
    username=user;
    password=pwd;
    minRunTime=Time.valueOf("00:30:00");
    try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      connection = DriverManager.getConnection(connectionString,username,password);
      connected=true;
      System.out.println("Connected");
      tp=new TreatmentPlantData(TpID,connection);
      ps=PumpingStationData.getPumpingStations(TpID,connection,minRunTime);
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

  @Override
  public void startScheduler()
  {
    Thread sch=new Thread(this);
    sch.start();
  }

  public void run()
  {
    //If treaament plan is off, switch off all pumping stations
    if(tp.getStatus().equals("OFF"))
    {

    }
    else{ //perform scheduling

    }
  }
}
