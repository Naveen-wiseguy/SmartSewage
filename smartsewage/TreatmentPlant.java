package smartsewage;
import java.net.*;
import java.io.*;
import java.util.*;

public class TreatmentPlant extends Thread{
  private int id;
  private int level;
  private Socket sock;
  private int incoming;
  private ArrayList<PumpingStation> stations;
  private int delay;
  private boolean alarm;
  private Thread runner;
  private Random rand;

  public TreatmentPlant(int id,Socket sock)
  {
    this.id=id;
    this.sock=sock;
    level=0;
    incoming=0;
    stations=new ArrayList<PumpingStation>();
    alarm=false;
    rand=new Random();
    delay=20;
  }

  public void addPumpingStation(PumpingStation station)
  {
    stations.add(station);
  }

  public void run()
  {
    System.out.println("[STP]: Started Level - "+level);
    while(!Thread.currentThread().isInterrupted()){
      level-=(rand.nextInt(10)+1);
      level+=(rand.nextInt(5)+1)*incoming;
      if(level>100)
        level=100;
      else if(level<0)
        level=0;
      StringBuilder data=new StringBuilder();
      data.append(id);
      data.append(" 000 ");
      data.append((char)getInputs());
      System.out.println("[STP]: Level - "+level);
      if(alarm)
        System.out.println("[STP]: Alarm raised");
      try{
        PrintWriter out=new PrintWriter(sock.getOutputStream(),true);
        out.println(data.toString());
        Thread.sleep(delay*1000);
      }
      catch(InterruptedException ex)
      {
        System.out.println("[STP]: interrupted");
        Thread.currentThread().interrupt();
      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
      }
      updateIncoming();
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
    System.out.println("Input :"+input);
    return input;
  }

  private void updateIncoming()
  {
    incoming=0;
    for(PumpingStation ps:stations)
    {
      if(ps.isPumpOn())
        incoming++;
    }
  }

}
