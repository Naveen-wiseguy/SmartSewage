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
  private String query="select * from treatment_plant_input where TpID=? and num=?";
  //private String query_ps="select * from pumping_station  where PsID=?";
  private String update_query="update treatment_plant_input set PsID=?, switchedOnAt=? where TpID=? and num=?";

  private String switch_off="update treatment_plant_input set PsID=NULL, switchedOnAt=NULL where TpID=? and num=?";
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
          status = "OFF";
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
  public void update_TPip(PumpingStationData PSD)
  {

    /*int new_pump_id = PSD.getPsID();
    if(status.equals("OFF")||new_pump_id != PsID)
    {*/
          update(PSD);
    /*}
    else
    {
      System.out.println("No new pumping station request");
    }
*/

  }


  public void update(PumpingStationData PSD)
  {
    /*Time dur_Lon = PSD.getDurationLastOn();
    Timestamp last_switched_offat_1 = PSD.getLastSwitchedOff();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    Time switched_on_at_1 = sdf.format(last_switched_offat_1) - sdf.format(dur_Lon) ;
    System.out.println(switched_on_at_1);
    */
    if(PSD.getPsID()!=PsID||status.equals("OFF")){
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
    if(status.equals("OFF"))
      return;
    status="OFF";
    System.out.println("Output "+num+" has been switched OFF");
    try{
      PreparedStatement ps=connection.prepareStatement(switch_off);
      ps.setInt(1,TpID);
      ps.setInt(2,num);
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }

  public String getStatus(Time MinRunTime)
  {
    duration=new Time(System.currentTimeMillis()-switchedOnAt.getTime());
    if(status.equals("OFF"))
    {
      return status;
    }
    else if(duration.getTime()>=MinRunTime.getTime())
    {
      status="UNLOCK";
    }
    else{
      status="LOCK";
    }
    //System.out.println("Status of PS :"+PsID+" = "+status+" at duration "+duration.getTime()/1000);
    return status;
  }

  public Time getDuration()
  {
    if(switchedOnAt==null)
      duration=new Time(0);
    else
      duration=new Time(System.currentTimeMillis()-switchedOnAt.getTime());
    return duration;
  }

  public int getPsID()
  {
    return PsID;
  }

  public int getNum()
  {
    return num;
  }
}
