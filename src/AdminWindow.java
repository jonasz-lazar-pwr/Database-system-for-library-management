import javax.swing.*;

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
