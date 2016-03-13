package smartsewage;


/**
*A class that will be used to decipher the sensor data sent by the micro processor
*/
public class SensorData{
  /**
  *The id of the pumping station whose data it represents
  */
  private int id;
  /**
  *The sensor inputs - upto 8 digital inputs
  */
  private byte[] inputs;

  /**
  * Static method to parse the network inputs and create an object of SensorData. Used when the data is encoded in bits of the byte
  * @param first The first byte sent over the network which contains the ID
  *@param second The second byte whcih contains the sensor data encoded into bits
  *@return ?Newly created object of type SensorData
  */
  static public SensorData parse(byte first,byte second)
  {
    SensorData obj=new SensorData();
    obj.id=first;
    byte x=1;
    obj.inputs=new byte[4];
    for(int i=0;i<4;i++,x<<=1)
    {
      if((x&second)==0)
        obj.inputs[i]=0;
      else
        obj.inputs[i]=1;
    }
    return obj;
  }

  /**
  * Static method to parse the network inputs and create an object of SensorData. Used when the data is ASCII encoded
  * @param line The string that will be parsed to extract the details
  *@return Newly created object of type SensorData
  */
  static public SensorData parse(String line)
  {
    SensorData obj=null;
    //System.out.println("Parsing string "+line);
    String[] parts=line.split("\\s+");
    //for(String s:parts)
      //System.out.println(s);
    int i=0;
    while(parts[i].equals(""))
      i++;
    if(parts.length-i<=1)
      return obj;
      //First string represents id
      try{
        byte first=Byte.parseByte(parts[i]);
        byte second=(byte)parts[i+2].charAt(0);
        obj=parse(first,second);
  }
  catch(NumberFormatException ex)
  {
    System.out.println("Number format error");
    System.out.println(ex.getMessage());
  }
    return obj;
  }

  public int getId()
  {
    return id;
  }

  public byte getInput(int index)
  {
    if(index<inputs.length)
      return inputs[index];
    else
      return -1;
  }

  public int getLevel()
  {
    for(int i=inputs.length-1;i>=0;i--)
      if(inputs[i]==1)
        return i+1;
    return 0;
  }

  @Override
  public String toString()
  {
    StringBuilder str=new StringBuilder();
    str.append("ID :"+id+"\n");
    for(int i=0;i<inputs.length;i++)
    {
      str.append("Input "+i+":"+inputs[i]+" ");
    }
    return str.toString();
  }


}
