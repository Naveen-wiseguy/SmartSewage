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
              MaxRunTime=result.getTime("MaxRunTime");
              OpRate=result.getInt("OpRate");
              System.out.println(MaxRunTime + " MaxRunTime");
              System.out.println(OpRate + " OutputRate");
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
    return OpRate;
  }


  }
