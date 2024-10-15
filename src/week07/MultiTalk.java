package week07;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MultiTalk {

    private final JFrame frame;

    private final String serverAddress;
    private final int serverPort;

    private JTextField t_id;
    private JTextField t_serverAddress;
    private JTextField t_serverPort;

    private JTextArea t_display;
    private JButton sendButton;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton exitButton;

    private Writer out;
    private Reader in;

    private Thread receiveThread = null;

    public MultiTalk(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        frame = new JFrame("P2P Free Talk");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 51111;

        new MultiTalk(serverAddress, serverPort);
    }

    /*
     * GUI related methods
     */
    private void buildGUI() {
        frame.add(createDisplayPanel(), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(3, 0));
        panel.add(createInputPanel());
        panel.add(createInfoPanel());
        panel.add(createControlPanel());

        frame.add(panel, BorderLayout.SOUTH);
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

        JTextField textField = new JTextField();
        sendButton = new JButton("보내기");
        sendButton.setEnabled(false);

        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        // Event Listeners
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    return;
                }

                sendMessage(text);
                textField.setText("");
            }
        };

        textField.addActionListener(listener);
        sendButton.addActionListener(listener);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();

        t_id = new JTextField("guest" + (int) (Math.random() * 100), 5);
        t_serverAddress = new JTextField(serverAddress, 5);
        t_serverPort = new JTextField(String.valueOf(serverPort), 5);

        panel.add(new JLabel("아이디: "));
        panel.add(t_id);
        panel.add(new JLabel("서버 주소: "));
        panel.add(t_serverAddress);
        panel.add(new JLabel("포트번호: "));
        panel.add(t_serverPort);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        connectButton = new JButton("접속하기");
        disconnectButton = new JButton("접속 끊기");
        disconnectButton.setEnabled(false);
        exitButton = new JButton("종료하기");

        panel.add(connectButton);
        panel.add(disconnectButton);
        panel.add(exitButton);

        // Event Listeners
        // 접속하기
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connectToServer();
                    sendMessage("/uid:" + t_id.getText());
                } catch (IOException ex) {
                    printDisplay("서버와의 연결 오류: " + ex.getMessage());
                    return;
                }

                sendButton.setEnabled(true);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                exitButton.setEnabled(false);
            }
        });

        // 접속 끊기
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                } catch (IOException ex) {
                    System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                    return;
                }

                printDisplay("서버와의 연결이 끊어졌습니다.");

                sendButton.setEnabled(false);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                exitButton.setEnabled(true);
            }
        });

        // 종료하기
        exitButton.addActionListener(new ActionListener() {
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
    private void sendMessage(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println("메시지 전송 오류: " + e.getMessage());
        }
    }

    private void receiveMessage() {
        try {
            String inMsg = ((BufferedReader) in).readLine();

            if (inMsg == null) {
                try {
                    disconnect();
                } catch (IOException ex) {
                    System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                    return;
                }

                printDisplay("서버와의 연결이 끊어졌습니다.");

                sendButton.setEnabled(false);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                exitButton.setEnabled(true);

                receiveThread = null;
                return;
            }

            printDisplay(inMsg);
        } catch (IOException e) {
            System.err.println("메시지 수신 오류: " + e.getMessage());
        }
    }

    private void connectToServer() throws IOException {
        Socket socket = new Socket();
        try {
            socket.connect(
                    new InetSocketAddress(t_serverAddress.getText(), Integer.parseInt(t_serverPort.getText())),
                    3000
            );
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        out = new BufferedWriter(osw);

        InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        in = new BufferedReader(isr);

        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (receiveThread == Thread.currentThread()) {
                    receiveMessage();
                }
            }
        });
        receiveThread.start();
    }

    private void disconnect() throws IOException {
        receiveThread = null;
        out.close();
    }
}
