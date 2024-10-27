package week09;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TextFileSender {

    private final JFrame frame;
    private final String serverAddress;
    private final int serverPort;

    private JTextArea t_display;
    private JButton sendButton;

    private Writer out;
//    private Reader in;

    public TextFileSender(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;   

        frame = new JFrame("TextFile Sender");

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

//                sendMessage(text);
//                printDisplay("나: " + text + "\n");
//                textField.setText("");

//                receiveMessage();

                sendFile(text);
            }
        };

        textField.addActionListener(listener);
        sendButton.addActionListener(listener);

        return panel;
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
                } catch (IOException ex) {
                    System.err.println("클라이언트 접속 오류: " + ex.getMessage());
                    return;
                }

                sendButton.setEnabled(true);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                exitButton.setEnabled(false);
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    disconnect();
                } catch (IOException ex) {
                    System.err.println("클라이언트 닫기 오류: " + ex.getMessage());
                    return;
                }

                sendButton.setEnabled(false);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                exitButton.setEnabled(true);
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
    private void sendMessage(String msg) {
//        try {
//            out.write(msg + "\n");
//            out.flush();
            ((PrintWriter) out).println(msg);
//        } catch (IOException e) {
//            System.err.println("메시지 전송 오류: " + e.getMessage());
//        }
    }

    private void sendFile(String fileName) {
        fileName = fileName.strip();
        if (fileName.isEmpty()) {
            return;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            printDisplay(">> 파일이 존재하지 않습니다. : " + fileName + "\n");
            return;
        }

        ((PrintWriter) out).println(fileName);

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

            String line;
            while ((line = br.readLine()) != null) {
                ((PrintWriter) out).println(line);
            }

            out.close();
        } catch (FileNotFoundException e) {
            printDisplay(">> 파일이 존재하지 않습니다. : " + e.getMessage() + "\n");
            return;
        } catch (IOException e) {
            printDisplay(">> 파일을 읽을 수 없습니다. : " + e.getMessage() + "\n");
            return;
        }
    }

//    private void receiveMessage() {
//        try {
//            String inMsg = ((BufferedReader) in).readLine();
//            printDisplay("서버: " + inMsg + "\n");
//        } catch (IOException e) {
//            System.err.println("메시지 수신 오류: " + e.getMessage());
//        }
//    }

    private void connectToServer() throws IOException {
        Socket socket = new Socket(serverAddress, serverPort);

        OutputStream os = socket.getOutputStream();
        out = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true);

//        InputStreamReader isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
//        in = new BufferedReader(isr);
    }

    private void disconnect() throws IOException {
        out.close();
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 51111;

        new TextFileSender(serverAddress, serverPort);
    }
}
