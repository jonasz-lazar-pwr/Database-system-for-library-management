import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Arrays;
import java.sql.Connection;
import java.util.Objects;
public class UserWindow extends JFrame{
    public UserWindow(){
        setTitle("Książkowość - panel czytelnika");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JLabel userLabel = new JLabel("W przyszłości będzie tu panel dla czytelnika");

        panel.add(userLabel);
        add(panel);

        setVisible(true);
    }

}