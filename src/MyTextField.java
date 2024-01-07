import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class MyTextField extends JTextField {
    public MyTextField(String text) {
        this.setText(text);
        this.setPreferredSize(new Dimension(200,55));
        this.setFont(new Font("Roboto", Font.PLAIN, 15));
        this.setForeground(Color.LIGHT_GRAY);
        this.setBackground(Color.DARK_GRAY);
        this.setCaretColor(Color.LIGHT_GRAY);
        this.setBorder(new LineBorder(new Color(55, 88, 159)));
        this.setHorizontalAlignment(0);
        this.setEditable(true);
    }
}
