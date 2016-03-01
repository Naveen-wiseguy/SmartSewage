package smartsewage;

import java.net.*;
import java.util.*;
import java.io.*;
/**
*Server for smart sewage to get data from the various clients and process it
*/
public class SmartSewageServer{

  private int port_num;
  private ServerSocket server;
  /**
  * Starts a ServerSocket object that will listen on default port (4000)
  */
  public SmartSewageServer() throws IOException
  {
    port_num=4000;
    server=new ServerSocket(port_num);
  }

  /**
  * Starts a ServerSocket object that will listen on the specified port
  * @param port_num The custom port number to listen on
  */
  public SmartSewageServer(int port_num) throws IOException
  {
    this.port_num=port_num;
    server=new ServerSocket(this.port_num);
  }

  /**
  * Starts a ServerSocket object that will listen on the specified port and specified IP address
  * @param port_num The custom port number to listen on
  * @param ip_addr The ip address as string
  */
  public SmartSewageServer(int port_num,String ip_addr) throws IOException
  {
    this.port_num=port_num;
    InetAddress address=InetAddress.getByName(ip_addr);
    server=new ServerSocket(this.port_num,50,address);
  }

  /**
  *Method starts the server and it will accept connections and extract the data from it
  */
  public void start()
  {
    while(true) //Infinitely listes to the socket until stopped
    {
      try{
      Socket socket=server.accept();
      char b;
      /*
      InputStream stream=socket.getInputStream();
      byte first,second;
      first=(byte)stream.read();
      second=(byte)stream.read();
      System.out.println("First :"+first+" Second :"+second);
      SensorData data=SensorData.parse(first,second);
      System.out.println(data.toString());
      */
      System.out.println("Got connected to client with IP :"+socket.getInetAddress().toString()+" Port :"+socket.getPort());
      SensorListener listener=new SensorListener(socket);
      listener.start();

      /*InputStream in=socket.getInputStream();
      while((b=(char)in.read())!=-1)
      {
        System.out.print(b);
      }*/
      /*BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line=reader.readLine();
      System.out.println(line);
      */
      //SensorData data=SensorData.parse(line);
      //System.out.println(data.toString());

      //in.close();
      //socket.close();
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }

  /**
  * Main method to test running of the server
  */
  static public void main(String[] args)
  {
    if(args.length!=5)
    {
      System.out.println("Five paramenters required : <Connection string> <username> <password> <treatment plant ID> <Scheduling interval in milliseconds>");
      return;
    }

    final SmartScheduler sch=new SmartScheduler(Integer.parseInt(args[3]),args[0],args[1],args[2],Long.parseLong(args[4]));
    SensorDataLogger logger=new SensorDataLogger(args[0],args[1],args[2]);
    CommandLogger cmdlogger=new CommandLogger(args[0],args[1],args[2]);
    Thread t=new Thread(new Runnable(){
      @Override
      public void run(){
        try{
        SmartSewageServer s=new SmartSewageServer(195);
        s.start();
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
      }
    });
    Thread t2=new Thread(new Runnable(){
      @Override
      public void run()
      {

          sch.startScheduler();
      }
    });
    t.start();
    t2.start();
    while(true) //Keeping the system running forever
    {

    }
  }

}
