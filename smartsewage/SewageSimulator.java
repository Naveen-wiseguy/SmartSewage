package smartsewage;

import java.net.*;
import java.io.*;
import java.util.*;

public class SewageSimulator{
  private PumpingStation[] stations;
  private TreatmentPlant stp;
  private int num;
  private String ip;

  public SewageSimulator(int num,String ip)
  {
    this.num=num;
    System.out.println("Do you want to set initial levels ?[yes/no]");
    Scanner s=new Scanner(System.in);
    String reply=s.nextLine();
    try{
      stp=new TreatmentPlant(num+1,new Socket(ip,195));
      if(reply.equals("yes"))
      {
        System.out.println("Enter level for treatment plant :");
        stp.setLevel(s.nextInt());
      }
      stp.startSim();
    }
    catch(IOException ex)
    {
      System.out.println(ex.getMessage());
      stp=null;
    }
    stations=new PumpingStation[num];
    for(PumpingStation ps:stations)
    {
      try{
        ps=new PumpingStation(new Socket(ip,195));
        if(stp!=null)
          stp.addPumpingStation(ps);
          if(reply.equals("yes"))
          {
            System.out.println("Enter level for Ps "+ps.getId()+" :");
            ps.setLevel(s.nextInt());
          }
        ps.startSim();
      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
        ps=null;
      }
    }
  }

  public SewageSimulator(int num,String ip,int delay)
  {
    this.num=num;
    System.out.println("Do you want to set initial levels ?[yes/no]");
    Scanner s=new Scanner(System.in);
    String reply=s.nextLine();
    try{
      stp=new TreatmentPlant(num+1,new Socket(ip,195));
      stp.setDelay(delay);
      if(reply.equals("yes"))
      {
        System.out.println("Enter level for treatment plant :");
        stp.setLevel(s.nextInt());
      }
      stp.startSim();
    }
    catch(IOException ex)
    {
      System.out.println(ex.getMessage());
      stp=null;
    }
    stations=new PumpingStation[num];
    for(PumpingStation ps:stations)
    {
      try{
        ps=new PumpingStation(new Socket(ip,195));
        if(stp!=null)
          stp.addPumpingStation(ps);
        ps.setDelay(delay);
        if(reply.equals("yes"))
        {
          System.out.println("Enter level for Ps "+ps.getId()+" :");
          ps.setLevel(s.nextInt());
        }
        ps.startSim();
      }
      catch(IOException ex)
      {
        System.out.println(ex.getMessage());
        ps=null;
      }
    }
  }

  public void stop()
  {
    for(PumpingStation ps:stations)
    {
      if(ps!=null)
        ps.stopSim();
    }
  }

  static public void main(String[] args)
  {
    try{
    Scanner s=new Scanner(System.in);
    System.out.println("Enter the number of pumping stations :");
    int num=s.nextInt();
    SewageSimulator sim=null;
    if(args.length==0)
      sim=new SewageSimulator(num,"localhost");
    else if(args.length==1)
      sim=new SewageSimulator(num,args[0]);
    else if(args.length==2)
      sim=new SewageSimulator(num,args[0],Integer.parseInt(args[1]));
  }
  catch(Exception ex)
  {
    System.out.println(ex.getMessage());
  }
  }

}
