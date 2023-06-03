import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private ArrayList<String> roomList;
    private Map<String, RoomChat> roomChats;
    private Registry registry;
    private ExecutorService pool;
    
    public ServerChat(Registry registry) throws RemoteException {
        roomList = new ArrayList<>();
        pool = Executors.newCachedThreadPool();
        this.registry = registry;
    }
    
    public ArrayList<String> getRooms() throws RemoteException {
        return roomList;
    }
    
    public void createRoom(String roomName) throws RemoteException {
        System.out.println("Creating room " + roomName);
        try {
            RoomChat room = new RoomChat(roomName);
            registry.bind(roomName, room);
            pool.execute(room);
            roomList.add(roomName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Created room " + roomName);
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

            registry.bind("Servidor", serverChat);

            System.out.println("Server is ready!");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}

