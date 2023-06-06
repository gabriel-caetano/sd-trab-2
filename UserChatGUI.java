import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class UserChatGUI extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JComboBox<String> roomComboBox;
    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton joinButton;
    private JButton leaveButton;
    private JButton createButton;

    private IUserChat user;
    private IRoomChat currentRoom;
    private IServerChat server;

    public UserChatGUI() {
        setTitle("CHAT RMI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
        setSize(600, 600);
        setLocationRelativeTo(null);
        connectToServer();
        pack();
    }

    private void initComponents() {
        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        // Painel superior
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Campo de nome de usuário
        JLabel usernameLabel = new JLabel("Nome de Usuário:");
        usernameField = new JTextField(15);
        topPanel.add(usernameLabel);
        topPanel.add(usernameField);

        // Botão de entrar na sala
        joinButton = new JButton("Entrar");
        joinButton.addActionListener(this);
        topPanel.add(joinButton);

        // Painel central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Área de mensagens
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel inferior
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Campo de entrada de texto
        inputField = new JTextField(30);
        inputField.addActionListener(this);
        bottomPanel.add(inputField, BorderLayout.CENTER);

        // Botão de enviar mensagem
        sendButton = new JButton("Enviar");
        sendButton.addActionListener(this);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Botão de sair da sala
        leaveButton = new JButton("Sair da Sala");
        leaveButton.addActionListener(this);
        leaveButton.setEnabled(false);
        bottomPanel.add(leaveButton, BorderLayout.WEST);

        // Botão de criar sala
        createButton = new JButton("Criar Sala");
        createButton.addActionListener(this);
        topPanel.add(createButton);

        // Painel lateral
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BorderLayout());
        mainPanel.add(sidePanel, BorderLayout.EAST);

        // ComboBox de salas
        JLabel roomLabel = new JLabel("Salas Disponíveis:");
        roomComboBox = new JComboBox<>();
        sidePanel.add(roomLabel, BorderLayout.NORTH);
        sidePanel.add(roomComboBox, BorderLayout.CENTER);
    }

    private void connectToServer() {
        // Configuração do RMI
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2020);
            server = (IServerChat) registry.lookup("Servidor");
            List<String> roomList = server.getRooms();
            for (String room : roomList) {
                roomComboBox.addItem(room);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinRoom() {
        String userName = usernameField.getText();
        String roomName = (String) roomComboBox.getSelectedItem();

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2020);
            currentRoom = (IRoomChat) registry.lookup(roomName);

            user = new UserChat(userName, this);
            currentRoom.joinRoom(userName, user);

            joinButton.setEnabled(false);
            leaveButton.setEnabled(true);
            usernameField.setEditable(false);
            roomComboBox.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leaveRoom() {
        try {
            String userName = usernameField.getText();
            currentRoom.leaveRoom(userName);

            joinButton.setEnabled(true);
            leaveButton.setEnabled(false);
            usernameField.setEditable(true);
            roomComboBox.setEnabled(true);

            user = null;
            currentRoom = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Digite o nome da nova sala:");
    
        try {
            server.createRoom(roomName);
            roomComboBox.addItem(roomName);
            roomComboBox.setSelectedItem(roomName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateMessageArea(String senderName, String message) {
        String currentText = messageArea.getText();
        messageArea.setText(currentText + senderName + ": " + message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private void sendMessage() {
        try {
            String userName = usernameField.getText();
            String message = inputField.getText();
    
            currentRoom.sendMsg(userName, message);
    
            inputField.setText("");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == joinButton) {
            joinRoom();
        } else if (e.getSource() == leaveButton) {
            leaveRoom();
        } else if (e.getSource() == createButton) {
            createRoom();
        } else if (e.getSource() == sendButton || e.getSource() == inputField) {
            sendMessage();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserChatGUI gui = new UserChatGUI();
                gui.setVisible(true);
            }
        });
    }
}
