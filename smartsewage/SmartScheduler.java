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
    minRunTime=new Time(60000);
    System.out.println("Min run time :"+minRunTime.getTime()/1000);
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
        System.out.println("Treatment plant is OFF");
        for(TreatmentPlantInput tpip:ip)
        {
          System.out.println("Treatment plant input : "+tpip.getNum()+" having "+tpip.getPsID());
          if(!tpip.getStatus(minRunTime).equals("OFF")){
            RelayCommand cmd=new RelayCommand((byte)tpip.getPsID());
            byte[] outputs={0,0,0,0};
            cmd.setOutputs(outputs);
            Publisher.getInstance().publishRelayCommand(cmd);
            tpip.switchOff();
            System.out.println("Switching OFF TP input :"+tpip.getNum()+"PSID: "+tpip.getPsID()+" command sent :"+cmd.toString());
          }
        }
      }
      else{ //perform scheduling

        System.out.println("Teratment plant ON");
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
          System.out.println("Duration: "+tpip.getDuration().getTime()+" Max run time :"+(long)station.getPump().getMaxRunTime().getTime()+ " Min time to empty :"+station.getMinTimeToEmpty().getTime());
          if(tpip.getStatus(minRunTime).equals("UNLOCK"))
          {
            if(tpip.getDuration().getTime()>(long)station.getPump().getMaxRunTime().getTime()|| station.getMinTimeToEmpty().getTime()<schedInterval)
            {
              System.out.println("Switching OFF due to excess time :"+tpip.getPsID());
              tpip.switchOff();
              RelayCommand cmd=new RelayCommand((byte)station.getPsID()); //Creating command to switch off
              byte[] outputs={0,0,0,0};
              cmd.setOutputs(outputs);
              Publisher.getInstance().publishRelayCommand(cmd); //Send the command to switch off
            }
          }

        }
        //Reassigning the OFF inputs and UNLOCKed inputs
        if(queue.size()>0)
        {
          for(TreatmentPlantInput tpip:ip)
          {
            if(tpip.getStatus(minRunTime).equals("OFF")&&queue.size()>0) // Try reassigning
            {
              PumpingStationData psToBeOn=queue.poll();
              System.out.println("Assigning to OFF input :"+psToBeOn.getPsID());
              ////////////ERROR LIES HERE I THINK////////////////////////
              tpip.update_TPip(psToBeOn);
              RelayCommand cmd=new RelayCommand((byte)psToBeOn.getPsID());
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
                System.out.println("Reassigning input from "+oldps.getPsID()+" to "+newps.getPsID());
                //Turn OFF the already running pump
                tpip.switchOff();
                RelayCommand cmd=new RelayCommand((byte)oldps.getPsID()); //Creating command to switch off
                byte[] outputs={0,0,0,0};
                cmd.setOutputs(outputs);
                Publisher.getInstance().publishRelayCommand(cmd); //Send the command to switch off
                //Turn ON the new pump
                tpip.update_TPip(newps);
                RelayCommand newcmd=new RelayCommand((byte)newps.getPsID()); //Command to switch ON the pumping station
                byte[] newoutputs={1,0,0,0};
                newcmd.setOutputs(newoutputs);
                Publisher.getInstance().publishRelayCommand(newcmd); //Sendign the command
                queue.remove(newps);
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
