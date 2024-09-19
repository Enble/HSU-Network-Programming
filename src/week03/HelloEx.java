package week03;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class HelloEx {

    private JFrame frame;
    private JTextField textField;
    private JLabel label;

    public HelloEx() {
        frame = new JFrame("HelloEx");

        buildGUI();

        frame.setSize(200, 80);
        frame.setLocation(500, 300);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildGUI() {
        label = new JLabel("Hello, Java!");
        frame.add(label, BorderLayout.SOUTH);
        frame.add(createInputPanel(), BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        textField = new JTextField(10);
        JButton button = new JButton("OK");

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = textField.getText();
                label.setText("Hello, " + name);

                textField.setText("");
            }
        });

        return panel;
    }

    /*
    private ActionListener handler = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = textField.getText();
            label.setText("Hello, " + name);

            textField.setText("");
        }
    };
    */
}
