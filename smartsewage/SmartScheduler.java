package smartsewage;

import java.util.*;
import java.sql.*;
import java.net.*;

public class SmartScheduler extends Scheduler implements Runnable{
  private TreatmentPlantData tp;
  private ArrayList<PumpingStationData> ps;
  private ArrayList<TreatmentPlantInput> ip;
  private PriorityQueue<PumpingStationData> queue;
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

  //Helper to retrieve a pump object of required ID
  private PumpingStationData getPumpingStation(int PsID)
  {
    for(PumpingStationData p:ps)
      if(p.getPsID()==PsID)
        return p;
    return null;
  }

  public SmartScheduler(int TpID,String conn,String user,String pwd,long schedInterval)
  {
    connectionString=conn;
    username=user;
    password=pwd;
    minRunTime=Time.valueOf("00:05:00");
    queue=new PriorityQueue<PumpingStationData>();
    this.schedInterval=schedInterval;
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
      ip=new ArrayList<TreatmentPlantInput>();
      PreparedStatement prepstmt=connection.prepareStatement("select num,TpID from treatment_plant_input where TpID=?");
      prepstmt.setInt(1,TpID);
      ResultSet result=prepstmt.executeQuery();
      while(result.next())
      {
        TreatmentPlantInput newtpip=new TreatmentPlantInput(result.getInt("num"),result.getInt("TpID"),connection);
        ip.add(newtpip);
      }
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
    while(true){
      //If treaament plan is off, switch off all pumping stations
      if(tp.getStatus().equals("OFF"))
      {
        for(TreatmentPlantInput tpip:ip)
        {
          if(!tpip.getStatus(minRunTime).equals("OFF")){
            tpip.switchOff();
            RelayCommand cmd=new RelayCommand((byte)tpip.getPsID());
            byte[] outputs={0,0,0,0};
            cmd.setOutputs(outputs);
            Publisher.getInstance().publishRelayCommand(cmd);
          }
        }
      }
      else{ //perform scheduling
        queue.clear();
        for(PumpingStationData station:ps)
        {
          if(station.getStatus().equals("AVAILABLE"))
            queue.add(station);
        }
        //Switching OFF the pumping stations that might be in danger
        for(TreatmentPlantInput tpip:ip)
        {
          if(tpip.getStatus(minRunTime).equals("OFF"))
            continue;
          PumpingStationData station=getPumpingStation(tpip.getPsID());
          if(tpip.getDuration().getTime()>station.getPump().getMaxRunTime().getTime()|| station.getMinTimeToEmpty().getTime()<schedInterval)
          {
            tpip.switchOff();
            RelayCommand cmd=new RelayCommand((byte)tpip.getPsID()); //Creating command to switch off
            byte[] outputs={0,0,0,0};
            cmd.setOutputs(outputs);
            Publisher.getInstance().publishRelayCommand(cmd); //Send the command to switch off
          }
        }
        //Reassigning the OFF inputs and UNLOCKed inputs
        if(queue.size()>0)
        {
          for(TreatmentPlantInput tpip:ip)
          {
            if(tpip.getStatus(minRunTime).equals("OFF")&&queue.size()>0) // Try reassigning
            {
              tpip.update_TPip(queue.poll());
              RelayCommand cmd=new RelayCommand((byte)tpip.getPsID());
              byte[] outputs={1,0,0,0};
              cmd.setOutputs(outputs);
              Publisher.getInstance().publishRelayCommand(cmd);
            }
            else if(tpip.getStatus(minRunTime).equals("UNLOCK")&&queue.size()>0)
            {
              PumpingStationData oldps=getPumpingStation(tpip.getPsID());
              PumpingStationData newps=queue.peek();
              if(newps.getLevel()>oldps.getLevel()) //Replace the pumping station
              {
                //Turn OFF the already running pump
                tpip.switchOff();
                RelayCommand cmd=new RelayCommand((byte)tpip.getPsID()); //Creating command to switch off
                byte[] outputs={0,0,0,0};
                cmd.setOutputs(outputs);
                Publisher.getInstance().publishRelayCommand(cmd); //Send the command to switch off
                //Turn ON the new pump
                tpip.update_TPip(queue.poll());
                cmd=new RelayCommand((byte)newps.getPsID()); //Command to switch ON the pumping station
                byte[] newoutputs={1,0,0,0};
                cmd.setOutputs(newoutputs);
                Publisher.getInstance().publishRelayCommand(cmd); //Sendign the command
              }
            }
          }
        }
      }
      try{
          Thread.sleep(schedInterval);
      }
      catch(InterruptedException ex)
      {
        ex.printStackTrace();
      }
    }

  }
}
