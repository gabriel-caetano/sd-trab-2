import java.rmi.*;
import java.util.List;

public interface IServerChat extends Remote {
  public List<String> getRooms() throws RemoteException;
  public void createRoom(String roomName) throws RemoteException;
}