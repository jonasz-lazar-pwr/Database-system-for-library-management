import java.awt.*;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class MyButton extends JButton {
    public MyButton(String text) {
        this.setText(text);
        this.setPreferredSize(new Dimension(200, 55));
        this.setFocusPainted(false);
        this.setContentAreaFilled(true);
        this.setHorizontalTextPosition(0);
        this.setVerticalTextPosition(0);
        this.setFont(new Font("Roboto", Font.PLAIN, 18));
        this.setForeground(Color.LIGHT_GRAY);
        this.setBackground(Color.DARK_GRAY);
        this.setBorder(new LineBorder(new Color(58, 88, 159)));
    }
}