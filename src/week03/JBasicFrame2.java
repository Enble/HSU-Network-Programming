package week03;

import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JFrame;

public class JBasicFrame2 extends JFrame {

    public JBasicFrame2() {
        super("Frame test 2");

        // 프레임 구성
//        buildGUI(this.getContentPane());

        this.setBounds(100, 200, 300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
