package smartsewage;

import java.sql.*;
import java.net.*;
//num is number of inputs
public class TreatmentPlantInput{
  private int TpID;
  private int PsID;
  private int num;
  private Timestamp switchedOnAt;
  private Time duration;
  private String status;
  private Connection connection;
  private String query="select * from treatment_plant_input where TpID=?, num=?";
  //private String query_ps="select * from pumping_station  where PsID=?";
  private String update_query="update treatment_plant_input set PsID=?, switchedOnAt=? where TpID=?, num=?";
  //A thread that will update the db at the required rate
  private Thread updater;
  public TreatmentPlantInput(int num,int TpID,Connection conn)
  {
    this.num=num;
    this.TpID=TpID;
    this.connection=conn;
    if(conn!=null){
      try{
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setInt(1,TpID);
        ps.setInt(2,num);
        ResultSet result=ps.executeQuery();
        if(result.next())
        {
          PsID = result.getInt("PsID");
          num = result.getInt("num");
          status = result.getString("status");
          switchedOnAt = result.getTimestamp("switchedOnAt");
        }
        else{
          System.out.println("Incorrect Id of the treatment plant");
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
    }
    else{
      System.out.println("Connection does  not exist");
    }
  }
  public void update_TPip(final PumpingStationData PSD, Socket sock)
  {

    int new_pump_id = PSD.getPsID();
    if(new_pump_id != PsID)
    {
      updater=new Thread(new Runnable(){
        public void run(){
          update(PSD);
        }
      });
      updater.start();
    }
    else
    {
      System.out.println("No new pumping station request");
    }


  }


  public void update(PumpingStationData PSD)
  {
    /*Time dur_Lon = PSD.getDurationLastOn();
    Timestamp last_switched_offat_1 = PSD.getLastSwitchedOff();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    Time switched_on_at_1 = sdf.format(last_switched_offat_1) - sdf.format(dur_Lon) ;
    System.out.println(switched_on_at_1);
    */
    if(PSD.getPsID()!=PsID){
      PsID=PSD.getPsID();
      switchedOnAt=new Timestamp(System.currentTimeMillis());
      duration=Time.valueOf("00:00:00");
      status="LOCK";
    }
    try{
      PreparedStatement ps=connection.prepareStatement(update_query);
      ps.setInt(1,PsID);
      ps.setTimestamp(2,switchedOnAt);
      ps.setInt(3,TpID);
      ps.setInt(4,num);
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }

  public void switchOff()
  {

  }

  public String getStatus(Time MinRunTime)
  {
    duration=new Time(System.currentTimeMillis()-switchedOnAt.getTime());
    if(duration.getTime()>=MinRunTime.getTime())
    {
      status="UNLOCK";
    }
    else{
      status="LOCK";
    }
    return status;
  }

  public Time getDuration()
  {
    duration=new Time(System.currentTimeMillis()-switchedOnAt.getTime());
    return duration;
  }
}
