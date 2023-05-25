import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.HashMap;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private ArrayList<String> roomList;
    private Map<String, RoomChat> roomChats;
    private Registry registry;
    private ExecutorService roomPool;
    
    public ServerChat(Registry registry) throws RemoteException {
        roomList = new ArrayList<>();
        roomPool = Executors.newCachedThreadPool();
        this.registry = registry;
    }
    
    public ArrayList<String> getRooms() throws RemoteException {
        return roomList;
    }
    
    public void createRoom(String roomName) throws RemoteException {
        roomList.add(roomName);
        RoomChat newRoom = new RoomChat(roomName);
        roomChats.put(roomName, newRoom);
    }

    public RoomChat geRoomChat(String chatName) throws RemoteException {
        return roomChats.containsKey(chatName) ? roomChats.get(chatName) : null ;
    }
    
    public static void main(String[] args) {
        try {
            int port = 2020;
            Registry registry = LocateRegistry.createRegistry(port);

            System.out.println("RMI registry created on port: " + port);

            ServerChat serverChat = new ServerChat(registry);
            IServerChat stub = (IServerChat) UnicastRemoteObject.exportObject(serverChat, 0);

            registry.bind("Servidor", stub);
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}

