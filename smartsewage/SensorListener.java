package smartsewage;

import java.net.*;
import java.io.*;
import java.util.*;

/**
*A listener class that will listen in a separate thread and receive the sensor data
*/
public class SensorListener extends Thread{

  /**
  *The socket to which the object listens
  */
  private Socket sock;

  public SensorListener(Socket sock)
  {
    this.sock=sock;
  }


  /**
  *Run method executed by the thread. It reads the data being sent on the stream and creates a SensorData Object and displays
  *the sensor data
  */
  @Override
  public void run()
  {
    try{
      BufferedReader reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
      SensorData data=null;
      while(!isInterrupted())
      {
        String line=reader.readLine();
        //System.out.println(line);
        if(line==null)
          break;
        data=SensorData.parse(line);
        if(data!=null)
        {//System.out.println(data.toString());
          Publisher pub=Publisher.getInstance();
          pub.publishSensorData(data,sock);
        }
        else{
          System.out.println("Parsing failed");

        }
      }
      System.out.println("Disconnecting from socket with IP :"+sock.getInetAddress().toString()+" Port :"+sock.getPort());
      reader.close();
      sock.close();
    }
    catch(IOException ex)
    {
      System.out.println(ex.getMessage());
    }
  }
}
