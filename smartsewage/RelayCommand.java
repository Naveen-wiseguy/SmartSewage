package smartsewage;

import java.util.*;

public class RelayCommand{
  private byte[] outputs;
  private byte id;

  public RelayCommand(byte id)
  {
    this.id=(byte)id;
    outputs=new byte[2];
    for(byte out:outputs)
      out=0;
  }

  public int getOutput(int pos)
  {
    if(pos>=outputs.length)
      return -1;
    else
      return outputs[pos];
  }

  public void setOutputs(byte[] outs)
  {
    for(int i=0;i<outputs.length;i++)
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
    byte[] outputs=new byte[2];
    if(!cmd.contains("RELAY"))
      return null;
    try{
    for(int i=0,j=5;i<outputs.length;i++,j++)
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

  public int getId()
  {
    return (int)id;
  }

}
