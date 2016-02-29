package smartsewage;

import java.net.*;
import java.util.*;

public class Publisher{
  private ArrayList<SensorDataListener> sensorListeners;
  private ArrayList<RelayCommandListener> commandListeners;

  static private final Publisher instance=new Publisher();

  private Publisher(){
    System.out.println("Created a new publisher");
    sensorListeners=new ArrayList<SensorDataListener>();
    commandListeners=new ArrayList<RelayCommandListener>();
  }

  static public Publisher getInstance()
  {
      return instance;

  }

  public void addSensorDataListener(SensorDataListener list)
  {
    //System.out.println("Added sensor data listener");
    sensorListeners.add(list);
    //System.out.println("The object is of type "+list.getClass().getName());
  }

  public void addRelayCommandListener(RelayCommandListener list)
  {
    System.out.println("Added relay command listener");
    commandListeners.add(list);
  }

  public void publishSensorData(SensorData data,Socket sock)
  {
    //System.out.println("Publishing data");
    for(SensorDataListener list:sensorListeners)
      list.sensorDataReceived(data,sock);
  }

  public void publishRelayCommand(RelayCommand command)
  {
    for(RelayCommandListener list:commandListeners)
      list.relayCommandReceived(command);
  }


}
