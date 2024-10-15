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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MultiChatServer {

    private final JFrame frame;
    private final int port;

    private JTextArea t_display;
    private JButton exitButton;

    private final Vector<ClientHandler> users = new Vector<>();
    private Thread acceptThread = null;

    private ServerSocket serverSocket;

    public MultiChatServer(int port) {
        this.port = port;

        frame = new JFrame("Multi Chat Server");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(900, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        int port = 51111;

        new MultiChatServer(port);
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
        JPanel panel = new JPanel(new GridLayout(0, 3));

        JButton connectButton = new JButton("서버 시작");
        JButton disconnectButton = new JButton("서버 종료");
        disconnectButton.setEnabled(false);
        exitButton = new JButton("종료하기");

        panel.add(connectButton);
        panel.add(disconnectButton);
        panel.add(exitButton);

        // Event Listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                });
                acceptThread.start();

                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                exitButton.setEnabled(false);
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();

                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                exitButton.setEnabled(true);
            }
        });
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
            printDisplay("서버가 시작되었습니다: " + InetAddress.getLocalHost().getHostAddress());

            while (acceptThread == Thread.currentThread()) {
                clientSocket = serverSocket.accept();
                printDisplay("클라이언트가 연결되었습니다: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                users.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            printDisplay("서버 소켓 종료");
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

    private void disconnect() {
        try {
            acceptThread = null;
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("서버 소켓 닫기 오류: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void printDisplay(String msg) {
        t_display.append(msg + "\n");
        t_display.setCaretPosition(t_display.getDocument().getLength());
    }

    private class ClientHandler extends Thread {
        private final Socket clientSocket;
        private String uid;
        private BufferedWriter out;

        private ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        private void sendMessage(String message) {
            try {
                out.write(uid + ": " + message + "\n");
                out.flush();
            } catch (IOException e) {
                System.err.println("서버 쓰기 오류: " + e.getMessage());
            }
        }

        private void broadcasting(String message) {
            for (ClientHandler user : users) {
                user.sendMessage(message);
            }
        }

        private void receiveMessages(Socket cs) {
            try {
                InputStreamReader isr = new InputStreamReader(cs.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(isr);

                OutputStreamWriter osw = new OutputStreamWriter(cs.getOutputStream(), StandardCharsets.UTF_8);
                out = new BufferedWriter(osw);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/uid:")) {
                        uid = message.substring(5);
                        printDisplay("새 참가자: " + uid);
                        printDisplay("현재 참가자 수: " + users.size());
                    } else {
                        printDisplay(uid + ": " + message);
                        broadcasting(message);
                    }
                }

                printDisplay(uid + "퇴장. 현재 참가자 수: " + users.size());
                users.remove(this);
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

        @Override
        public void run() {
            receiveMessages(clientSocket);
        }
    }
}
