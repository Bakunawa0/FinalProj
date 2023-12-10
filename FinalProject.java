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

class EDWindow extends JDialog { // both the encrypt and decrypt windows look the same so we use the same class for both
    public EDWindow(JFrame parent, boolean encryptMode, String key) {
        super(parent, (encryptMode ? "Encrypt" : "Decrypt") + " Messages", true); // but depending on the value of encryptMode we change some aspects
        setLocationRelativeTo(parent);
        setResizable(false);
        setSize(400, 300);
        
        String keyString = (encryptMode ? "Encryption" : "Decryption") + " Key"; // the text that will label the keyField

        JLabel messageLabel = new JLabel("Message");
        JLabel resultLabel = new JLabel("Result");
        JTextArea messageArea = new JTextArea();
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        JLabel keyLabel = new JLabel(keyString);
        JTextField keyField = new JTextField(key);
        JButton eDButton = new JButton(encryptMode ? "Encrypt" : "Decrypt"); // what the button says also needs to change

        eDButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String outputString = ""; // this string will hold the output text
                String newKey = keyField.getText(); // if the key is different than the one set in the Settings, this will update the value
                String message = messageArea.getText().toUpperCase(); // store the message as all upper case so we don't have to deal with lower case
                message = message.replaceAll("\\s", ""); // use regular expression to replace all whitespace (whitespace is spaces, newlines, and tabs) with nothing, effectively deleting all whitespace
                int[] keyDigits = keyToNumbers(newKey); // convert the key into numbers
                if (!encryptMode) { // decrypt mode
                    String[] messageChunks = chunk(message, newKey.length()); // break the message into chunks the length of the key
                    String[] reversedChunks = flipChunks(messageChunks); // reverse the chunks
                    message = ""; // blank out the message so we can overwrite it
                    for (String chunk : reversedChunks) { // for every chunk in reversedChunks
                        message += chunk; // add it to the message without gaps. now message holds all the chunks stuck together
                    }
                    for (int i = 0; i < keyDigits.length; i++) {
                        keyDigits[i] *= -1; // multiply every digit in the key by -1 so that shiftLetters will subtract instead of add
                    }
                    outputString += shiftLetters(message.replaceAll("0", ""), keyDigits); // shift the letters using keyDigits and at the same time removing the 0s
                } else { // encrypt mode
                    String shiftedLetters = shiftLetters(message, keyDigits); // shift the letters using keyDigits
                    outputString +=  shiftedLetters + "\n\n"; // put it in the output

                    String[] messageChunks = chunk(shiftedLetters, newKey.length()); // break shiftedLetters into chunks the length of the key
                    for (String chunk : messageChunks) {
                        outputString += chunk + " "; // put the chunks in the output seperated by spaces
                    }
                    outputString += "\n\n";

                    String[] reversedChunks = flipChunks(messageChunks); // reverse the chunks
                    for (String chunk : reversedChunks) {
                        outputString += chunk + " "; // put the reversed chunks in the output too
                    }
                    outputString += "\n\n";

                    for (String reversedChunk : reversedChunks) {
                        outputString += reversedChunk; // stick all the reversed chunks together
                    }
                }
                resultArea.setText(outputString); // display the output
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
        String rawDigits = ""; // stores the raw values of the key
        k = k.toUpperCase(); // so we don't have to account for lower case
        for (int letter : k.toCharArray()) {
            rawDigits += letter - 64; // A = 65 in ASCII so we can't use the value directly, but if we subtract 65 then A would be 0, so we subtract 64
        }
        
        int[] digits = new int[rawDigits.length()]; // this will store the actual digits we will use
        for (int i = 0; i < digits.length; i++) { // go through the array and the string character by character, thus breaking 19 into 1 9 for example
            digits[i] = rawDigits.charAt(i) - '0'; // the values in the string are characters so like before we subtract '0' to make them numbers
        } // now every item in the array is a single digit

        return digits;
    }

    // the second step of the encryption
    public String shiftLetters(String message, int[] key) {
        String output = ""; // this will hold the shifted result
        char[] array = message.toCharArray(); // this makes it so that we can modify individual letters more easily than with a string
        for (int i = 0; i < array.length; i++) { // go through the array
            // System.out.print(array[i] + " + " + key[i % key.length] + " = ");
            array[i] += key[i % key.length]; // shift the letter at i by the key at i. the % ensures that the key index is never greater than key.length and instead loops back to 0 if i = key.length
            if (array[i] > 'Z') { // if the shift put us past Z
                array[i] -= 26; // subtract 26 to get back to the letters
            } else if (array[i] < 'A') { // if the shift put us before A
                array[i] += 26; // add 26 to get back to the letters
            }
            // System.out.println(array[i]);
        }

        output = String.valueOf(array); // convert the array back into a string
        // System.out.println("output: " + output);
        return output;
    }

    // the third step of the encryption
    public String[] chunk(String message, int size) {
        String[] chunks = new String[Math.ceilDiv(message.length(), size)]; // Math.ceilDiv will divide its arguments and round up to the nearest integer. this will give an array with the appropriate number of chunks
         // the 0 padding step
        while (message.length() % size != 0) { // as long as the length of the message doesn't evenly divide by size
            message += "0"; // add 0s
        } //System.out.println(message);
        
        for (int i = 0; i < chunks.length; i++) { // go through the chunks
            chunks[i] = message.substring(0, size); // take a substring from the message that goes from the first character to size, creating a chunk
            // System.out.print(chunks[i] + " ");
            message = message.substring(size, message.length()); // take a substring that goes from size to the end of the string and set message to that, deleting the chunk we just made from message
        }
        /*
         * Visualization: let's say the size is 4
         * THISISASECRET000
         * |0 |size (the first argument counts the first character as 0 while the second argument counts the first character as 1)
         * so "THIS" becomes a chunk and gets stored in chunks
         * THISISASECRET000
         *     |size      |message.length()
         * so "ISASECRET000" gets stored back into message
         */

        return chunks;
    }

    // the fourth step of the encryption
    public String[] flipChunks(String[] chunks) {
        // String[] reversedChunks = new String[chunks.length];
        for (int i = 0; i < chunks.length; i++) {
            String reversedChunk = ""; // this will hold the reversed string
            for (int j = chunks[i].length() - 1; j >= 0; j--) { // go backwards through the string
                reversedChunk += chunks[i].charAt(j); // store the characters in reverse order in reversedChunk
            }
            chunks[i] = reversedChunk; // replace the chunk there with the reversed chunk
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