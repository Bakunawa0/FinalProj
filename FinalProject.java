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
                String outputString = "";
                String newKey = keyField.getText();
                String message = messageArea.getText().toUpperCase();
                message = message.replaceAll("\\s", "");
                int[] keyDigits = keyToNumbers(newKey);
                if (!encryptMode) {
                    for (int i = 0; i < keyDigits.length; i++) {
                        keyDigits[i] *= -1;
                    }
                    outputString += shiftLetters(message, keyDigits);
                } else {
                    String shiftedLetters = shiftLetters(message, keyDigits);
                    outputString +=  shiftedLetters + "\n\n";

                    String[] messageChunks = chunk(shiftedLetters, newKey.length());
                    for (String chunk : messageChunks) {
                        outputString += chunk + " ";
                    }
                    outputString += "\n\n";

                    String[] reversedChunks = flipChunks(messageChunks);
                    for (String chunk : reversedChunks) {
                        outputString += chunk + " ";
                    }
                    outputString += "\n\n";

                    for (String reversedChunk : reversedChunks) {
                        outputString += reversedChunk;
                    }
                }
                resultArea.setText(outputString);
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

    // the first step of the encryption
    public int[] keyToNumbers(String k) {
        String rawDigits = "";
        k = k.toUpperCase();
        for (int letter : k.toCharArray()) {
            rawDigits += letter - 64; // A = 65 in ASCII so we can't use the value directly, but if we subtract 65 then A would be 0, so we subtract 64
        }
        
        int[] digits = new int[rawDigits.length()];
        for (int i = 0; i < digits.length; i++) {
            digits[i] = rawDigits.charAt(i) - '0';
        }

        return digits;
    }

    // the second step of the encryption
    public String shiftLetters(String message, int[] key) {
        String output = "";
        char[] array = message.toCharArray();
        for (int i = 0; i < array.length; i++) {
            // System.out.print(array[i] + " + " + key[i % key.length] + " = ");
            array[i] += key[i % key.length];
            if (array[i] > 'Z') {
                array[i] -= 26;
            } else if (array[i] < 'A') {
                array[i] += 26;
            }
            // System.out.println(array[i]);
        }

        output = String.valueOf(array);
        // System.out.println("output: " + output);
        return output;
    }

    // the third step of the encryption
    public String[] chunk(String message, int size) {
        String[] chunks = new String[Math.ceilDiv(message.length(), size)];

        while (message.length() % size != 0) {
            message += "0";
        } //System.out.println(message);
        
        for (int i = 0; i < chunks.length; i++) {
            chunks[i] = message.substring(0, size);
            // System.out.print(chunks[i] + " ");
            message = message.substring(size, message.length());
        } //System.out.println(chunks.length);

        return chunks;
    }

    // the fourth step of the encryption
    public String[] flipChunks(String[] chunks) {
        // String[] reversedChunks = new String[chunks.length];
        for (int i = 0; i < chunks.length; i++) {
            String reversedChunk = "";
            for (int j = chunks[i].length() - 1; j >= 0; j--) {
                reversedChunk += chunks[i].charAt(j);
            }
            chunks[i] = reversedChunk;
        }
        return chunks;
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