import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

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

        // Need to reference the parent of these UI components later
        FinalProject parent = this;

        // open encrypt menu
        encryptItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (key.equals("")) {
                    JOptionPane.showConfirmDialog(parent, "Please set a key in the \"Settings\" menu.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                final EDWindow eDWindow = new EDWindow(parent, true, key);
                eDWindow.setVisible(true);
            }
        });

        // open decrypt menu
        decryptItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (key.equals("")) {
                    JOptionPane.showConfirmDialog(parent, "Please set a key in the \"Settings\" menu.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    return;
                }
                final EDWindow eDWindow = new EDWindow(parent, false, key);
                eDWindow.setVisible(true);
            }
        });

        // settings option opens SettingsDialog
        settingsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final SettingsDialog settingsDialog = new SettingsDialog(parent, key);
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

class EDWindow extends JDialog {

    public static ArrayList<Character> keyToChar = new ArrayList<>();
    public static ArrayList<Character> cryptText = new ArrayList<>();

    public static ArrayList<Integer> numericValues = new ArrayList<>();

    public static ArrayList<Integer> cryptKey = new ArrayList<>();

    public static ArrayList<Character> encrypted = new ArrayList<>();
    public static ArrayList<Character> tempList = new ArrayList<>();
    public static ArrayList<Character> encryptWithZero = new ArrayList<>();
    public static ArrayList<Character> Reverse = new ArrayList<>();

    public EDWindow(JFrame parent, boolean encryptMode, String key) {
        super(parent, (encryptMode ? "Encrypt" : "Decrypt") + " Messages", true);
        setLocationRelativeTo(parent);
        setResizable(false);
        setSize(400, 300);

        String keyString = (encryptMode ? "Encryption" : "Decryption") + " Key";

        JLabel messageLabel = new JLabel("Message");
        JLabel resultLabel = new JLabel("Result");
        JTextArea messageArea = new JTextArea();
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JLabel keyLabel = new JLabel(keyString);
        JTextField keyField = new JTextField(key);
        JButton eDButton = new JButton(encryptMode ? "Encrypt" : "Decrypt");

        eDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyToChar.clear();
                cryptText.clear();
                numericValues.clear();
                cryptKey.clear();
                encrypted.clear();
                tempList.clear();
                encryptWithZero.clear();
                Reverse.clear();
                resultArea.setText("");

                String newKey = keyField.getText();
                String message = messageArea.getText();
                
                if (!messageArea.getText().isEmpty() && messageArea.getText().length() >= newKey.length()) {
                    keyToNumbers(newKey);
                    encrypt(message);

                    for (int i = 0; i < encrypted.size(); i++) {
                        resultArea.append(String.valueOf(encrypted.get(i)));
                    }

                    resultArea.append("\n");
                    reverseEncrypt(newKey);

                    //new reverse
                    for (int i = 0; i < encryptWithZero.size(); i++) {
                        resultArea.append(String.valueOf(encryptWithZero.get(i)));
                    }

                    resultArea.append("\n");


                    for (int i = 0; i < Reverse.size(); i++) {
                        resultArea.append(String.valueOf(Reverse.get(i)));
                        if ((i + 1) % newKey.length() == 0 && i + 1 != Reverse.size()) {
                            resultArea.append(" ");
                        }
                    }


                    resultArea.append("\n");

                    //prints joined group
                    for(int x=0;x< Reverse.size();x++){
                        resultArea.append(String.valueOf(Reverse.get(x)));
                    }
                } else {
                    JOptionPane.showMessageDialog(parent,"Invalid Message");
                }
            }
        });

        add(messageLabel);
        add(resultLabel);
        add(new JScrollPane(messageArea));
        add(new JScrollPane(resultArea));
        add(keyLabel);
        add(new JSeparator());
        add(keyField);
        add(new JSeparator());
        add(eDButton);
        setLayout(new GridLayout(0, 2));
    }

    // the first step of the encryption/decryption
    public void keyToNumbers(String k) {

        k = k.toUpperCase();
        char[] initial = k.toCharArray();


        for (int x = 0; x < initial.length; x++) {

            if (initial[x] == ' ') {
                continue;
            }
            else {
                keyToChar.add(initial[x]);
            }
        }

        for (int x = 0; x < keyToChar.size(); x++) {
            char currentChar = keyToChar.get(x);

            int numericValue = currentChar - 'A' + 1;
            numericValues.add(numericValue);

            String numString = String.valueOf(numericValue);

            for (int i = 0; i < numString.length(); i++) {
                int digit = Character.getNumericValue(numString.charAt(i));
                cryptKey.add(digit);
            }
        }
    }

    //encrypt conversion
    public void encrypt(String e) {
        int keyIndex = 0;
        e = e.toUpperCase();
        char[] Text = e.toCharArray();

        //checks if text is letter
        for (int x = 0; x < Text.length; x++) {

            if (Character.isLetter(Text[x])) {
                cryptText.add(Text[x]);
            }
            else {
                continue;
            }
        }

        //converts currentchar then adds to arraylist
        for (int x = 0; x < cryptText.size(); x++) {
            char currentChar = cryptText.get(x);

            // loop back cryptKey
            int currentKey = cryptKey.get(keyIndex);
            keyIndex = (keyIndex + 1) % cryptKey.size();

            char converted = (char) ((currentChar - 'A' + currentKey) % 26 + 'A');
            encrypted.add(converted);
        }
    }

    public void reverseEncrypt(String q) {

        int keyElement = q.length();
        int groupSize = (encrypted.size() + keyElement - 1) / keyElement;

        encryptWithZero.addAll(encrypted);
        if (encryptWithZero.size() % keyElement != 0) {
            for (int i = encryptWithZero.size(); i < keyElement * groupSize; i++) {
                encryptWithZero.add('0');
            }
        }

        Reverse.addAll(encryptWithZero);
        for (int i = 0; i < groupSize; i++) {
            reverseMethod(keyElement, i);
        }
    }

    public static void reverseMethod(int keyElement, int num) {
        int startNum = num*keyElement;
        int endNum = ((num+1)*keyElement)-1;

        for (int i = startNum; i <= endNum; i++) {
            tempList.add(Reverse.get(i));
        }
        Collections.reverse(tempList);

        for (int i = startNum; i <= endNum; i++) {
            Reverse.set(i, tempList.get(i - startNum));
        }

        for (int i = startNum; i <= endNum; i++) {
            System.out.print(Reverse.get(i));
        }
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
                    JOptionPane.showConfirmDialog(parent, "Key must exist and be composed of alphabetic characters only", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    keyField.setText(key); // revert to the last key
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setLayout(new GridLayout(3, 1));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(enDecLabel);
        add(keyField);
        add(buttonPanel);
    }
}
