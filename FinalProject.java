import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FinalProject extends JFrame {
    private static String key = "";

    public FinalProject() {
        super("Cryptographical Program");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create File Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        // options
        JMenu textSubMenu = new JMenu("Text");
        JMenuItem encryptItem = new JMenuItem("Encrypt");
        JMenuItem decryptItem = new JMenuItem("Decrypt");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem exitItem = new JMenuItem("Exit");
        // adding the options to the menu
        textSubMenu.add(encryptItem);
        textSubMenu.add(decryptItem);
        fileMenu.add(textSubMenu);
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        // adding the menu to the frame
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // settings option opens SettingsDialog
        settingsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final SettingsDialog settingsDialog = new SettingsDialog(null, key);
                settingsDialog.setVisible(true);
            }
        });

        // make exit button do what it says
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }
    // SettingsDialog needs to set the key so we need a setter
    public static void setKey(String k) {
        key = k;
    }

    public static void main(String[] args) {
        FinalProject fp = new FinalProject();
    }
}

class SettingsDialog extends JDialog {
    public SettingsDialog(JFrame parent, String key) {
        super(parent, "Settings", true);
        setLocationRelativeTo(parent);
        setResizable(false);
        setSize(300, 100);

        JLabel enDecLabel = new JLabel("Encryption / Decryption Key");
        JTextField keyField = new JTextField(key);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tentativeKey = keyField.getText();
                if (tentativeKey.matches("[A-Za-z]+")) { // this weird string is called regex and it's basically a common way to select certain parts of a string. in this case we are selecting from A to Z and a to z with the + meaning we do this continuously
                    FinalProject.setKey(tentativeKey); // if the regex matches properly then we can save the key
                    dispose();
                } else {
                    // otherwise we show an error
                    JOptionPane.showConfirmDialog(parent, "Key must be alphabetic characters only", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    keyField.setText(key); // revert to the last key
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        setLayout(new BorderLayout());
        add(enDecLabel, BorderLayout.NORTH);
        add(keyField, BorderLayout.CENTER);
        add(saveButton, BorderLayout.LINE_END);
        add(cancelButton, BorderLayout.SOUTH);
    }
}