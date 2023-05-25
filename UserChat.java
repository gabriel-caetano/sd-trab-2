import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UserChat extends UnicastRemoteObject implements IUserChat {
  private String userName;
  
  public UserChat(String userName) throws RemoteException {
      this.userName = userName;
  }
  
  public void deliverMsg(String senderName, String msg) throws RemoteException {
      System.out.println("Mensagem recebida de " + senderName + ": " + msg);
  }
  
  public static void main(String[] args) {
      try {
          String serverURL = "rmi://172.21.36.46/Servidor";
          IServerChat server = (IServerChat) Naming.lookup(serverURL);
          
          List<String> roomList = server.getRooms();
          System.out.println("Lista de salas disponíveis:");
          for (String room : roomList) {
              System.out.println(room);
          }
          
          // Escolha da sala pelo usuário
          String selectedRoom = ""; // Supondo que o usuário tenha selecionado uma sala
          
          IRoomChat room = (IRoomChat) Naming.lookup("rmi://localhost/" + selectedRoom);
          IUserChat user = new UserChat("Nome do Usuário");
          room.joinRoom("Nome do Usuário", user);
          
          // Enviar mensagens para a sala
          room.sendMsg("Nome do Usuário", "Olá, pessoal!");
          
          // Sair da sala
          room.leaveRoom("Nome do Usuário");
      } catch (Exception e) {
          System.err.println("Erro: " + e.getMessage());
      }
  }
}