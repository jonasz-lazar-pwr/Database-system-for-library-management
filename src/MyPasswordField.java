import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MyPasswordField extends JPasswordField {
    public MyPasswordField(String text, int mainR, int mainG, int mainB) {
        this.setText(text);
        this.setEchoChar((char) 0); // Wyłącz kropki
        this.setPreferredSize(new Dimension(200,55));
        this.setFont(new Font("Roboto",Font.PLAIN, 15));
        this.setForeground(Color.LIGHT_GRAY);
        this.setBackground(Color.DARK_GRAY);
        this.setCaretColor(Color.LIGHT_GRAY);
        this.setBorder(new LineBorder(new Color(mainR, mainG, mainB)));
        this.setHorizontalAlignment(0);
    }
}