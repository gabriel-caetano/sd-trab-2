import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RoomChat extends UnicastRemoteObject implements IRoomChat, Runnable {
  private String roomName;
  private Map<String, IUserChat> userList;
  
	public void run() {
		System.out.println("running chat room " + this.roomName);
	}

  public RoomChat(String roomName) throws RemoteException {
      this.roomName = roomName;
      userList = new HashMap<>();
  }
  
  public void sendMsg(String usrName, String msg) throws RemoteException {
      System.out.println("Mensagem enviada por " + usrName + ": " + msg);
      
      for (IUserChat user : userList.values()) {
          user.deliverMsg(usrName, msg);
      }
  }
  
  public void joinRoom(String usrName, IUserChat user) throws RemoteException {
      userList.put(usrName, user);
  }
  
  public void leaveRoom(String usrName) throws RemoteException {
      userList.remove(usrName);
  }
  
  public void closeRoom() throws RemoteException {
      for (IUserChat user : userList.values()) {
          user.deliverMsg("Servidor", "Sala fechada pelo servidor.");
      }
  }
  
  public String getRoomName() throws RemoteException {
      return roomName;
  }
}