package smartsewage;

import java.sql.*;
import java.net.*;
//num is number of inputs
public class TreatmentPlantInput implements SensorDataListener,Runnable{
  private int TpID;
  private int PsID;
  private int num;
  private Time switchedOnAt;
  private Time duration;
  private String status;
  private Connection connection;
  private String query="select * from treatment_plant_input where TpID=?";
  //private String query_ps="select * from pumping_station  where PsID=?";
  private String update_query="update treatment_plant_input set PsID=?, switchedOnAt=? ,duration=?, status=? where TpID=?";
  //A thread that will update the db at the required rate
  private Thread updater;
  public TreatmentPlantInputData(Scheduler,Connection conn)
  {
    this.TpID=TpID;
    this.connection=conn;
    if(conn!=null){
      try{
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setInt(1,TpID);
        ResultSet result=ps.executeQuery();
        if(result.next())
        {
          pumpID = result.getInt("PsID");
          num_1 = result.getInt("num");
          status_1 = result.getString("status");
          switched_on_at = result.getTime("switchedOnAt");
          duration_1 = result.getTime("duration");

        }
        else{
          System.out.println("Incorrect Id of the treatment plant");
        }
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      }
      updater=new Thread(this);
      Publisher.getInstance().addSensorDataListener(this);
    }
    else{
      System.out.println("Connection does  not exist");
    }
  }
  public update_TPip(PumpingStationData PSD, Socket sock)
  {

    int new_pump_id = PSD.getPsID();
    if(new_pump_id != pumpID)
    {
      updater.start();
    }
    else
    {
      System.out.println("No new pumping station request");
    }


  }

  public void run()
  {
    update();
  }

  public void update()
  {
    Time dur_Lon = PSD.getDurationLastOn();
    Timestamp last_switched_offat_1 = PSD.getLastSwitchedOff();

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    Time switched_on_at_1 = sdf.format(last_switched_offat_1) - sdf.format(dur_Lon) ;
    System.out.println(switched_on_at_1);

    try{
      PreparedStatement ps=connection.prepareStatement(update_query);
      ps.setInt(1,new_id);
      ps.setTime(2,switched_on_at_1);
      ps.setInt(3,dur_Lon);
      ps.setString(4,status)
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }
}
