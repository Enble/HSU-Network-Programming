package week06;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TimeServerGUI {

    private final JFrame frame;
    private final int port;

    private JTextArea t_display;

    private ServerSocket serverSocket;

    public TimeServerGUI(int port) {
        this.port = port;

        frame = new JFrame("TimeServer GUI");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(900, 300);
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
        JPanel panel = new JPanel(new GridLayout(1, 0));

        JButton exitButton = new JButton("종료");
        panel.add(exitButton);

        // Event Listeners
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    System.err.println("서버 닫기 오류: " + ex.getMessage());
                    System.exit(-1);
                }
                System.exit(0);
            }
        });

        return panel;
    }

    /*
     * socket related methods
     */
    private void startServer() {
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            t_display.append("서버가 시작되었습니다.\n");

            while (true) {
                clientSocket = serverSocket.accept();
                t_display.append("클라이언트가 연결되었습니다.\n");

                receiveMessages(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("서버 닫기 오류: " + e.getMessage());
            }
        }
    }

    private void receiveMessages(Socket cs) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(cs.getOutputStream(), StandardCharsets.UTF_8);
            BufferedWriter out = new BufferedWriter(osw);

            String message = cs.getInetAddress().getHostAddress() + ": " + new Date();
            printDisplay("클라이언트 메시지: " + message + "\n");
            out.write(message + "\n");
            out.flush();

            printDisplay("클라이언트가 연결을 종료했습니다." + "\n");
        } catch (IOException e) {
            System.err.println("서버 읽기 오류: " + e.getMessage());
        } finally {
            try {
                cs.close();
            } catch (IOException e) {
                System.err.println("서버 닫기 오류: " + e.getMessage());
            }
        }
    }

    private void printDisplay(String msg) {
        t_display.append(msg);
        t_display.setCaretPosition(t_display.getDocument().getLength());
    }

    public static void main(String[] args) {
        int port = 51111;

        new TimeServerGUI(port).startServer();
    }
}
