import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MyPasswordField extends JPasswordField {
    public MyPasswordField(String text) {
        this.setText(text);
        this.setEchoChar((char) 0);
        this.setPreferredSize(new Dimension(200,55));
        this.setFont(new Font("Roboto",Font.PLAIN, 15));
        this.setForeground(Color.LIGHT_GRAY);
        this.setBackground(Color.DARK_GRAY);
        this.setCaretColor(Color.LIGHT_GRAY);
        this.setBorder(new LineBorder(new Color(55, 88, 159)));
        this.setHorizontalAlignment(0);
    }
}