package week03;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ByteClientGUI {

    private final JFrame frame;

    public ByteClientGUI() {
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

        JTextArea textArea = new JTextArea("클라이언트 화면");
        textArea.setEditable(false);

        scrollPane.setViewportView(textArea);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField textField = new JTextField();
        JButton button = new JButton("보내기");

        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        // Event Listeners
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ...
            }
        });
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ...
            }
        });

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        JButton connectButton = new JButton("접속하기");
        JButton disconnectButton = new JButton("접속 끊기");
        JButton exitButton = new JButton("종료하기");

        panel.add(connectButton);
        panel.add(disconnectButton);
        panel.add(exitButton);

        // Event Listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ...
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ...
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ...
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        new ByteClientGUI();
    }
}
