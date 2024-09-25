package project;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ByteClientGUI {

    private final JFrame frame;
    private JTextArea t_display;

    private String serverAddress;
    private int serverPort;
    private OutputStream out;

    public ByteClientGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;   

        frame = new JFrame("ByteClient GUI");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildGUI() {
        frame.add(createDisplayPanel(), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(2, 0));
        panel.add(createInputPanel());
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
        JButton button = new JButton("보내기");

        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        // Event Listeners
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    return;
                }

                int message;
                try {
                    message = Integer.parseInt(text);
                } catch (NumberFormatException ex) {
                    textField.setText("");
                    return;
                }

                sendMessage(message);

                t_display.append("나: " + message + "\n");
                textField.setText("");
            }
        };

        textField.addActionListener(listener);
        button.addActionListener(listener);

        return panel;
    }

    private void sendMessage(int msg) {
        try {
            out.write(msg);
        } catch (IOException e) {
            System.err.println("클라이언트 쓰기 오류: " + e.getMessage());
            System.exit(-1);
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        JButton connectButton = new JButton("접속하기");
        JButton disconnectButton = new JButton("접속 끊기");
        disconnectButton.setEnabled(false);
        JButton exitButton = new JButton("종료하기");

        panel.add(connectButton);
        panel.add(disconnectButton);
        panel.add(exitButton);

        // Event Listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connectToServer();
                    connectButton.setEnabled(false);
                    disconnectButton.setEnabled(true);
                    exitButton.setEnabled(false);
                } catch (IOException ex) {
                    System.err.println("클라이언트 접속 오류: " + ex.getMessage());
                }
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    exitButton.setEnabled(true);
                } catch (IOException ex) {
                    System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return panel;
    }

    private void connectToServer() throws IOException {
        Socket socket = new Socket(serverAddress, serverPort);
        out = socket.getOutputStream();
    }

    private void disconnect() throws IOException {
        out.close();
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 51111;

        new ByteClientGUI(serverAddress, serverPort);
    }
}
