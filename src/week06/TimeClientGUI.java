package week06;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TimeClientGUI {

    private final JFrame frame;
    private final String serverAddress;
    private final int serverPort;

    private JTextArea t_display;
    private JButton sendButton;

    private Writer out;
    private Reader in;

    public TimeClientGUI(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;   

        frame = new JFrame("TimeClient GUI");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /*
     * GUI related methods
     */
    private void buildGUI() {
        frame.add(createDisplayPanel(), BorderLayout.CENTER);
        frame.add(createControlPanel(), BorderLayout.SOUTH);
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

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JButton connectButton = new JButton("접속하기");
        JButton exitButton = new JButton("종료하기");

        panel.add(connectButton);
        panel.add(exitButton);

        // Event Listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connectToServer();
                    receiveMessage();
                    disconnect();
                } catch (IOException ex) {
                    System.err.println("클라이언트 접속 오류: " + ex.getMessage());
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

    private void printDisplay(String msg) {
        t_display.append(msg);
        t_display.setCaretPosition(t_display.getDocument().getLength());
    }

    /*
     * socket related methods
     */
    private void receiveMessage() {
        try {
            String inMsg = ((BufferedReader) in).readLine();
            printDisplay("서버: " + inMsg + "\n");
        } catch (IOException e) {
            System.err.println("메시지 수신 오류: " + e.getMessage());
        }
    }

    private void connectToServer() throws IOException {
        Socket socket = new Socket(serverAddress, serverPort);
        OutputStream os = socket.getOutputStream();

        InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        in = new BufferedReader(isr);
    }

    private void disconnect() throws IOException {
        out.close();
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 51111;

        new TimeClientGUI(serverAddress, serverPort);
    }
}
