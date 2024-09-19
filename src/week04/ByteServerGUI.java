package week04;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ByteServerGUI {

    private final JFrame frame;

    public ByteServerGUI() {
        frame = new JFrame("ByteServer GUI");

        buildGUI();

        frame.setSize(400, 300);
        frame.setLocation(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildGUI() {
        frame.add(createDisplayPanel(), BorderLayout.CENTER);
        frame.add(createControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        JTextArea textArea = new JTextArea("서버 화면");
        textArea.setEditable(false);

        scrollPane.setViewportView(textArea);

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
                // ...
            }
        });

        return panel;
    }

    public static void main(String[] args) {
        new ByteServerGUI();
    }
}
