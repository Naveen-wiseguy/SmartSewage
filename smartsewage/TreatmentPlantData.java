package smartsewage;

import java.sql.*;
import java.net.*;

public class TreatmentPlantData implements SensorDataListener,Runnable{
  private int TpID;
  private int level;
  private String status;
  private Connection connection;
  private String query="select * from treatment_plant where TpID=?";
  private String update_query="update treatment_plant set level=?, status=? where TpID=?";
  //A thread that will update the db at the required rate
  private Thread updater;
  public TreatmentPlantData(int TpID,Connection conn)
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
          level=result.getInt("level");
          status=result.getString("status");
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

  public void sensorDataReceived(SensorData data,Socket sock)
  {
    if(data.getId()==TpID)
    {
      int old=level;
      level=data.getLevel();
      //Setting status based on level
      if(level>=4)
      {
        status="OFF";
      }
      else{
        status="ON";
      }
      //update the db if there is a change
      if(old!=level)
      {
        updater=new Thread(this);
        updater.start();
      }
    }
  }

  public void run()
  {
    update();
  }

  public void update()
  {
    try{
      PreparedStatement ps=connection.prepareStatement(update_query);
      ps.setInt(1,level);
      ps.setString(2,status);
      ps.setInt(3,TpID);
      ps.execute();
    }
    catch(SQLException se)
    {
      se.printStackTrace();
    }
  }

  public int getLevel()
  {
    return level;
  }

  public int getTpId()
  {
    return TpID;
  }

  public String getStatus()
  {
    return status;
  }
}
