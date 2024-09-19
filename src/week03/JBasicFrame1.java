package week03;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class JBasicFrame1 {

    private JFrame frame;

    public JBasicFrame1() {
        frame = new JFrame("Frame test 1");

        buildGUI();

//        jFrame.setSize(300, 200);
//        jFrame.setLocation(100, 200);
        frame.setBounds(100, 200, 200, 300);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void buildGUI() {
        frame.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton btn;
        for (int i=0; i<5; i++) {
            btn = new JButton("" + (i + 1));
//            btn.setBounds(0, 40 * i, 100, 30);
            frame.add(btn);
        }
    }
}
