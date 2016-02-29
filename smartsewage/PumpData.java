package smartsewage;

import java.sql.*;
import java.net.*;

public class PumpData{
  private int PumpID;
  private Time maxRunTime;
  private int opRate;
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
              maxRunTime=result.getTime("MaxRunTime");
              opRate=result.getInt("OpRate");
              System.out.println(maxRunTime + " MaxRunTime");
              System.out.println(opRate + " OutputRate");
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

  public int getOpRate()
  {
    return opRate;
  }

  public Time getMaxRunTime()
  {
    return maxRunTime;
  }


  }
