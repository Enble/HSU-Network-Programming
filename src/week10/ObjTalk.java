package week10;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ObjTalk extends JFrame {
    private String serverAddress;
    private int serverPort;
    private String uid;

    private JTextField t_input;
    private JTextField t_userId;
    private JTextField t_serverAddress;
    private JTextField t_serverPort;

    private JTextArea t_display;
    private JButton b_send;
    private JButton b_connect;
    private JButton b_disconnect;
    private JButton b_exit;

    private Socket socket;
    private ObjectOutputStream out;

    private Thread receiveThread = null;

    public ObjTalk(String serverAddress, int serverPort) {
        super("Object Talk");

        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        buildGUI();

        setSize(500, 400);
        setLocation(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 51111;

        new ObjTalk(serverAddress, serverPort);
    }

    /*
     * GUI related methods
     */
    private void buildGUI() {
        add(createDisplayPanel(), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(3, 0));
        panel.add(createInputPanel());
        panel.add(createInfoPanel());
        panel.add(createControlPanel());

        add(panel, BorderLayout.SOUTH);
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        t_display = new JTextArea();
        t_display.setEditable(false);

        scrollPane.setViewportView(t_display);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        t_input = new JTextField();
        b_send = new JButton("보내기");
        b_send.setEnabled(false);

        panel.add(t_input, BorderLayout.CENTER);
        panel.add(b_send, BorderLayout.EAST);

        // Event Listeners
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };

        t_input.addActionListener(listener);
        b_send.addActionListener(listener);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        t_userId = new JTextField(7);
        t_serverAddress = new JTextField(12);
        t_serverPort = new JTextField(5);

        t_userId.setText("guest" + getLocalAddress().split("\\.")[3]);
        t_serverAddress.setText(this.serverAddress);
        t_serverPort.setText(String.valueOf(this.serverPort));

        t_serverPort.setHorizontalAlignment(JTextField.CENTER);

        panel.add(new JLabel("아이디: "));
        panel.add(t_userId);
        panel.add(new JLabel("서버 주소: "));
        panel.add(t_serverAddress);
        panel.add(new JLabel("포트번호: "));
        panel.add(t_serverPort);

        return panel;
    }

    private void setUi(boolean isOn) {
        if (isOn) {
            b_connect.setEnabled(false);
            b_disconnect.setEnabled(true);

            t_input.setEnabled(true);
            b_send.setEnabled(true);
            b_exit.setEnabled(false);

            t_userId.setEditable(false);
            t_serverAddress.setEditable(false);
            t_serverPort.setEditable(false);
        } else {
            b_connect.setEnabled(true);
            b_disconnect.setEnabled(false);

            t_input.setEnabled(false);
            b_send.setEnabled(false);
            b_exit.setEnabled(true);

            t_userId.setEditable(true);
            t_serverAddress.setEditable(true);
            t_serverPort.setEditable(true);
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        b_connect = new JButton("접속하기");
        b_disconnect = new JButton("접속 끊기");
        b_disconnect.setEnabled(false);
        b_exit = new JButton("종료하기");

        panel.add(b_connect);
        panel.add(b_disconnect);
        panel.add(b_exit);

        // Event Listeners
        // 접속하기
        b_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ObjTalk.this.serverAddress = t_serverAddress.getText();
                ObjTalk.this.serverPort = Integer.parseInt(t_serverPort.getText());

                try {
                    connectToServer();
                    sendUserId();
                } catch (IOException ex) {
                    printDisplay("서버와의 연결 오류: " + ex.getMessage());
                    return;
                }

                setUi(true);
            }
        });

        // 접속 끊기
        b_disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                } catch (IOException ex) {
                    System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                    return;
                }

                printDisplay("서버와의 연결이 끊어졌습니다.");
                setUi(false);
            }
        });

        // 종료하기
        b_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return panel;
    }

    private void printDisplay(String msg) {
        t_display.append(msg + "\n");
        t_display.setCaretPosition(t_display.getDocument().getLength());
    }

    /*
     * socket related methods
     */
    private String getLocalAddress() {
        InetAddress local;
        String address = "";
        try {
            local = InetAddress.getLocalHost();
            address = local.getHostAddress();
            System.out.println(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void send(ChatMsg msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            System.err.println("메시지 전송 오류: " + e.getMessage());
        }
    }

    private void sendUserId() {
        uid = t_userId.getText();
        send(new ChatMsg(uid, ChatMsg.MODE_LOGIN));
    }

    private void sendMessage() {
        String message = t_input.getText();
        if (message.isEmpty()) {
            return;
        }

        send(new ChatMsg(uid, ChatMsg.MODE_TX_STRING, message));

        t_input.setText("");
    }

    private void connectToServer() throws IOException {
        socket = new Socket();
        SocketAddress sa = new InetSocketAddress(serverAddress, serverPort);
        socket.connect(sa, 3000);

        out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        receiveThread = new Thread(new Runnable() {
            private ObjectInputStream in;

            private void receiveMessage() {
                try {
                    ChatMsg inMsg = (ChatMsg) in.readObject();

                    if (inMsg == null) {
                        try {
                            disconnect();
                        } catch (IOException ex) {
                            System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                            return;
                        }

                        printDisplay("서버와의 연결이 끊어졌습니다.");
                        setUi(false);

                        return;
                    }

                    switch (inMsg.mode) {
                        case ChatMsg.MODE_TX_STRING:
                            printDisplay(inMsg.userId + ": " + inMsg.message);
                            break;
                    }
                } catch (IOException e) {
                    printDisplay("연결을 종료했습니다.");
                } catch (ClassNotFoundException e) {
                    printDisplay("잘못된 객체가 전달되었습니다.");
                }
            }

            @Override
            public void run() {
                try {
                    in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                } catch (IOException e) {
                    printDisplay("입력 스트림이 열리지 않음");
                }

                while (receiveThread == Thread.currentThread()) {
                    receiveMessage();
                }
            }
        });
        receiveThread.start();
    }

    private void disconnect() throws IOException {
        send(new ChatMsg(uid, ChatMsg.MODE_LOGOUT));

        receiveThread = null;
        out.close();
    }
}
