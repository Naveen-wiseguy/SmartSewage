import java.net.*;
import java.io.*;
import java.util.*;

public class ServerTest{
  static public void main(String[] args)
  {
    try{
      System.out.println("Enter the data line to be sent :");
      Scanner scanner=new Scanner(System.in);
      Socket sock=null;
      if(args.length==0)
        sock=new Socket("52.26.211.106",195);
      else
        sock=new Socket(args[0],195);
      OutputStream output=sock.getOutputStream();
      PrintWriter writer=new PrintWriter(output,true);
      byte[] bytes={0x12,0x00};
      System.out.println("2 OVERLD "+(char)0x80);
      writer.println("2 OVERLD "+(char)0x80);
      writer.println(scanner.nextLine());
      writer.close();
      output.close();
      sock.close();
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
    }
  }
}
