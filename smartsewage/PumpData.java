package smartsewage;

import java.sql.*;
import java.net.*;

public class PumpData{
  private int PumpID;
  private Time MaxRunTime;
  private int OpRate;
  private Connection connection;

  private String query="select * from pump where PumpID=?";

  public PumpData(int PumpID,Connection conn)
  {
    this.PumpID=PumpID;
    this.connection=conn;
    if(conn!=null)
    {
      try {
            PreparedStatement ps=connection.prepareStatement(query);
            ps.setInt(1,PumpID);
            ResultSet result=ps.executeQuery();
                if(result.next())
                {
              time_1=result.getTime("MaxRunTime");
              oprate=result.getInt("OpRate");
              System.out.println(time_1 + "MaxRunTime");
              System.out.println(oprate + "OutputRate");
            }
                else{
                  System.out.println("Incorrect ID");
                    }
          }
      catch(SQLException se)
          {
            se.printStackTrace();
          }
        }
    else{
        System.out.println("Connection does not exist");
    }
  }


  }


}
