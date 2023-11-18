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
public class AdminWindow extends JFrame{
    public AdminWindow(){
        setTitle("Książkowość - zarządzanie biblioteką");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JLabel adminLabel = new JLabel("W przyszłości będzie tu panel dla bibliotekarza");

        panel.add(adminLabel);
        add(panel);

        setVisible(true);
    }

}
