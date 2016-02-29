package smartsewage;
import java.net.*;
import java.io.*;
import java.util.*;

public class PumpingStation implements Runnable{
  /*8
  *Static variable to track ids of pumping stations
  */
  private static byte next=1;
  /**
  * The id of the current pumping station*/
  private byte id;
  /**
  *The level of water in the pumping station*/
  private int level;
  /**
  *The socket connection to be used to send data to the server
  */
  private Socket sock;
  /**
  *Indicates whether the pump is on or not
  */
  private boolean pump_on;
  /**
  *Random object that will be used for random number generation
  */
  private Random rand;
  /**
  *The thread that will perform the simulation of water level
  */
  private Thread runner;
  /**
  *The delay between sensor data being sent
  */
  private int delay;
  /**
  *Alarm in case of very high overflow
  */
  private boolean alarm;

  public PumpingStation() throws IOException
  {
    id=next;
    next++;
    sock=new Socket("localhost",195);
    level=0;
    pump_on=false;
    rand=new Random();
    delay=20;
    alarm=false;
  }

  public PumpingStation(Socket sock)
  {
    id=next;
    next++;
    this.sock=sock;
    level=0;
    pump_on=false;
    rand=new Random();
    delay=20;
    alarm=false;
  }

  public void run()
  {
    System.out.println("[Pumping Station "+id+"]: Started Level - "+level+" Sensor output - "+getInputs());
    Thread t=new Thread(new Runnable(){
      public void run()
      {
        readSocket();
      }
    });
    t.start();
    while(!Thread.currentThread().isInterrupted()){
      level+=rand.nextInt(5)+1;
      if(pump_on)
      {
        level-=(rand.nextInt(10)+1);
        System.out.println("[Pumping station "+id+"]:Pump running");
      }

      if(level>100)
        level=100;
      else if(level<0)
        level=0;
      StringBuilder data=new StringBuilder();
      data.append(id);
      data.append(" 000 ");
      data.append((char)getInputs());
      System.out.println("[Pumping station "+id+"]: Level - "+level);
      if(alarm)
        System.out.println("[Pumping station "+id+"]: Alarm raised");
      try{
        //sock.setSoTimeout(1000);
        PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
        out.println(data.toString());
        Thread.sleep(delay*1000);
        /*BufferedReader reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String command=reader.readLine();
        byte[] outputs=RelayCommand.parseString(command);
        if(outputs!=null)
        {
          if(outputs[0]==1)
            pump_on=true;
          else
            pump_on=false;
        }*/
      }
      catch(InterruptedException ex)
      {
        System.out.println("[Pumping station "+id+"]: interrupted");
        Thread.currentThread().interrupt();
      }
      catch(SocketTimeoutException ex)
      {

      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
      }

    }
  }

  public void readSocket()
  {
    while(true)
    {
      try{
        System.out.println("Listening for command for PS: "+id);
        sock.setSoTimeout(1000);
        BufferedReader reader=new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String command=reader.readLine();
        byte[] outputs=RelayCommand.parseString(command);
        if(outputs!=null)
        {
          System.out.println("Received a command for PS :"+id);
          if(outputs[0]==1)
            pump_on=true;
          else
            pump_on=false;
        }
      }
      catch(SocketTimeoutException ex)
      {

      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }

  public void startSim()
  {
    runner=new Thread(this);
    runner.start();
  }

  public void stopSim()
  {
    try{
    runner.interrupt();
    sock.close();
  }
  catch(IOException ex)
  {
    System.out.println(ex.getMessage());
  }
  }

  public void setDelay(int delay)
  {
    this.delay=delay;
  }

  public void setPumpOn(boolean pump_on)
  {
    this.pump_on=pump_on;
  }

  public boolean isPumpOn()
  {
    return pump_on;
  }

  private byte getInputs()
  {
    byte input=0x70;
    alarm=false;
    if(level>20)
      input=(byte)(input|1);
    if(level>45)
      input=(byte)(input|2);
    if(level>70)
      input=(byte)(input|4);
    if(level>95){
      input=(byte)(input|8);
      alarm=true;
    }
    return input;
  }

}
