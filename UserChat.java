import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class UserChat extends UnicastRemoteObject implements IUserChat {
	private String userName;
	

	public UserChat(String userName) throws RemoteException {
		this.userName = userName;
	}

	public void deliverMsg(String senderName, String msg) throws RemoteException {
		System.out.println("Mensagem recebida de " + senderName + ": " + msg);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Chatter");
		JTextField textField = new JTextField(50);
		JTextArea messageArea = new JTextArea(16, 50);
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
		frame.pack();
		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 2020);
			IServerChat server = (IServerChat) registry.lookup("Servidor");

			List<String> roomList = server.getRooms();
			System.out.println("Lista de salas disponíveis:");
			for (String room : roomList) {
				System.out.println(room);
			}

			// Escolha da sala pelo usuário
			// String selectedRoom = ""; // Supondo que o usuário tenha selecionado uma sala

			// IUserChat user = new UserChat("Nome do Usuário");
			// room.joinRoom("Nome do Usuário", user);

			// // Enviar mensagens para a sala
			// room.sendMsg("Nome do Usuário", "Olá, pessoal!");

			// // Sair da sala
			// room.leaveRoom("Nome do Usuário");
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
		} finally {
			frame.setVisible(false);
			frame.dispose();
		}
	}
}