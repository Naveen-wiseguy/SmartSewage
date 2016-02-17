package smartsewage;

import java.util.*;

public class RelayCommand{
  private byte[] outputs;
  private byte id;

  public RelayCommand(byte id)
  {
    this.id=(byte)id;
    outputs=new byte[4];
    for(byte out:outputs)
      out=0;
  }

  public void setOutputs(byte[] outs)
  {
    for(int i=0;i<4;i++)
      outputs[i]=outs[i];
  }

  public String toString()
  {
    StringBuilder str=new StringBuilder();
    str.append("RELAY");
    for(byte out:outputs)
      str.append(out);
    str.append("T");
    return str.toString();
  }

  static public byte[] parseString(String cmd)
  {
    byte[] outputs=new byte[4];
    if(!cmd.contains("RELAY"))
      return null;
    try{
    for(int i=0,j=5;i<4;i++,j++)
    {
      outputs[i]=Byte.parseByte(Character.toString(cmd.charAt(j)));
    }
    }
    catch(NumberFormatException ex)
    {
      System.out.println(ex.getMessage());
      outputs=null;
    }
    return outputs;
  }

}
