package smartsewage;

import java.sql.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
* The class used to store data about the the pumping station . It listens to the sensor data, relay commands and can be compared ot sort based on levels and priority
*/

public class PumpingStationData implements SensorDataListener,RelayCommandListener, Comparable<PumpingStationData>{
  private int PsID;
  private String location;
  private int priority;
  private long capacity; //capacity in ml
  private int level;
  private Timestamp lastSwitchedOff;
  private Time durationLastOn;
  private Time minTimeToEmpty;
  private Time minRunTime;
  private Timestamp switchedOnAt;
  private String status;
  private Connection connection;

  //The pump corresponding to the pumping station
  private PumpData pump;
  //The last received command to be sent periodically in response to the sensor data
  private RelayCommand lastCommand;
  //private PumpData pump;
  private Socket sock;
  //A thread that will update the db at the required rate
  private Thread updater,dispatcher;

  private String query="select * from pumping_station where PsID=?";

  private String query_update_sensor="update pumping_station set level=?, minTimeToEmpty=?, status= ? where PsID=?";
  private String query_update_relay="update pumping_station set durationLastOn=? , lastSwitchedOff=? , status=? where PsID=?";

  public PumpingStationData(int PsID,Connection conn,Time minRunTime)
  {
    this.PsID=PsID;
    this.connection=conn;
    this.minRunTime=minRunTime;
    if(conn!=null)
    {
      try{
        PreparedStatement ps=connection.prepareStatement(query);
        ps.setInt(1,PsID);
        ResultSet result=ps.executeQuery();
        if(result.next())
        {
          location=result.getString("location");
          priority=result.getInt("priority");
          capacity=result.getInt("capacity");
          level=result.getInt("level");
          lastSwitchedOff=result.getTimestamp("lastSwitchedOff");
          durationLastOn=result.getTime("durationLastOn");
          minTimeToEmpty=result.getTime("minTimeToEmpty");
          status=result.getString("status");
          int pumpId=result.getInt("PumpID");
          pump=new PumpData(pumpId,connection);
        }
        else{
          System.out.println("Incorrect ID");
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
      Publisher.getInstance().addSensorDataListener(this);
    }
    else{
        System.out.println("Connection does not exist");
    }
  }

  public void sensorDataReceived(SensorData data,Socket sock)
  {
    this.sock=sock;
    if(data.getId()==PsID)
    {
      int old=level;
      level=data.getLevel();
      if(old!=level){
        updater=new Thread(new Runnable(){
          public void run()
          {
            PumpingStationData.this.update();
          }
        });
        updater.start();
      }
    }
  }

  public void relayCommandReceived(final RelayCommand command)
  {
    if(command.getId()==PsID)
    {
      dispatcher=new Thread(new Runnable(){
        public void run(){
          PumpingStationData.this.dispatch(command);
        }
      });
      dispatcher.start();
    }
  }

  public void update()
  {
    //Calculate min time to empty
    minTimeToEmpty=new Time(((level*capacity/5)/pump.getOpRate())*1000);
    if(status.equals("OFF")){
      if((minTimeToEmpty.getTime()>=minRunTime.getTime())&&System.currentTimeMillis()-lastSwitchedOff.getTime()>=durationLastOn.getTime())
      {
        status="AVAILABLE";
      }
    }
      try{
        System.out.println("Level = "+level);
        PreparedStatement ps=connection.prepareStatement(query_update_sensor);
        ps.setInt(1,level);
        ps.setTime(2,minTimeToEmpty);
        ps.setString(3,status);
        ps.setInt(4,PsID);
        ps.execute();
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
      dispatch(lastCommand);
  }

  public void dispatch(RelayCommand command)
  {
    try{
      //Sending command to the board
      PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
      out.println(command.toString());
      //Updating status locally
      String prev=status;
      if(command.getOutput(0)==1){
        if(!status.equals("ON"))
        {
          status="ON";
          switchedOnAt=new Timestamp(System.currentTimeMillis());
        }
      }
      else{
        if(status.equals("ON"));
        {
          status="OFF";
          lastSwitchedOff=new Timestamp(System.currentTimeMillis());
          durationLastOn=new Time(lastSwitchedOff.getTime()-switchedOnAt.getTime());
        }
      }
      if(prev!=status) //if status has changed, update database
      {
        //update the database
        try{
          System.out.println("Level = "+level);
          PreparedStatement ps=connection.prepareStatement(query_update_relay);
          ps.setTime(1,durationLastOn);
          ps.setTimestamp(2,lastSwitchedOff);
          ps.setString(3,status);
          ps.setInt(4,PsID);
          ps.execute();
        }
        catch(SQLException se)
        {
          se.printStackTrace();
        }
      }
    }
    catch(IOException ex)
    {
      System.out.println(ex.getMessage());
    }
  }

  public void run()
  {
    update();
  }

  public int compareTo(PumpingStationData ps)
  {
    if(this.level==ps.level)
    {
      return ps.priority-this.priority;
    }
    else
      return ps.level-this.level;
  }

  //Getter methods
  public int getPsID()
  {
    return PsID;
  }

  public int getLevel()
  {
    return level;
  }

  public int getPriority()
  {
    return priority;
  }

  public long getCapacity()
  {
    return capacity;
  }

  public String getLocation()
  {
    return location;
  }

  public Time getDurationLastOn()
  {
    return durationLastOn;
  }

  public Time getMinTimeToEmpty()
  {
    return minTimeToEmpty;
  }

  public Timestamp getLastSwitchedOff()
  {
    return lastSwitchedOff;
  }

  public String getStatus()
  {
    return status;
  }

  /*public PumpData getPump()
  {
    return pump;
  }*/

  /**
  * Static method used to get and generate all the pumping stations belonging to a given treatment plant
  */
  public static ArrayList<PumpingStationData> getPumpingStations(int TpID,Connection conn,Time minRunTime)
  {
    ArrayList<PumpingStationData> list=new ArrayList<PumpingStationData>();
    if(conn!=null)
    {
      try{
        String query="select PsID from pumping_station where TpID=?";
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setInt(1,TpID);
        ResultSet rs=ps.executeQuery();
        while(rs.next())
        {
          int PsID=rs.getInt("PsID");
          PumpingStationData new_pumping_station=new PumpingStationData(PsID,conn,minRunTime);
          list.add(new_pumping_station);
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }

    }
    else{
      System.out.println("No connection to database");
    }
    return list;
  }

}
