    
// GUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

public class GUI {
    private JFrame frame;
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;
    private JComboBox<String> algoBox;

    public void createAndShowGUI() {
        frame = new JFrame("Simple Encrypt/Decrypt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 420);
        frame.setLocationRelativeTo(null);

        // Controls (compact)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        algoBox = new JComboBox<>(new String[] { "Caesar", "AES" });
        keyField = new JTextField(6);
        keyField.setToolTipText("Integer key for Caesar (optional)");
        top.add(new JLabel("Algo:"));
        top.add(algoBox);
        top.add(new JLabel("Caesar key:"));
        top.add(keyField);

        // Text areas
        inputArea = new JTextArea(8, 48);
        outputArea = new JTextArea(8, 48);
        outputArea.setEditable(false);
        inputArea.setLineWrap(true);
        outputArea.setLineWrap(true);

        JScrollPane inScroll = new JScrollPane(inputArea);
        JScrollPane outScroll = new JScrollPane(outputArea);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton encBtn = new JButton("Encrypt");
        JButton decBtn = new JButton("Decrypt");
        JButton clearBtn = new JButton("Clear");
        JButton copyBtn = new JButton("Copy Output");

        encBtn.addActionListener(e -> onEncrypt());
        decBtn.addActionListener(e -> onDecrypt());
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        copyBtn.addActionListener(e -> copyOutputToClipboard());

        buttons.add(encBtn);
        buttons.add(decBtn);
        buttons.add(clearBtn);
        buttons.add(copyBtn);

        // Layout
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout(6, 6));
        center.add(inScroll, BorderLayout.NORTH);
        center.add(outScroll, BorderLayout.SOUTH);

        frame.setLayout(new BorderLayout(6, 6));
        frame.add(top, BorderLayout.NORTH);
        frame.add(center, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void onEncrypt() {
        String algo = (String) algoBox.getSelectedItem();
        String text = inputArea.getText();
        if (text == null)
            text = "";
        try {
            if ("Caesar".equals(algo)) {
                int key = 3;
                try {
                    key = Integer.parseInt(keyField.getText().trim());
                } catch (Exception ignored) {
                }
                outputArea.setText(CryptoApp.caesarEncrypt(text, key));
            } else {
                // AES: get two-line bundle (cipherBase64 \n keyBase64)
                String bundle = CryptoApp.aesEncryptBundle(text);
                outputArea.setText(bundle);
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void onDecrypt() {
        String algo = (String) algoBox.getSelectedItem();
        String text = inputArea.getText();
        if (text == null)
            text = "";
        try {
            if ("Caesar".equals(algo)) {
                int key = 3;
                try {
                    key = Integer.parseInt(keyField.getText().trim());
                } catch (Exception ignored) {
                }
                outputArea.setText(CryptoApp.caesarDecrypt(text, key));
            } else {
                // Expect two non-empty lines: cipherBase64 then keyBase64
                String[] lines = text.split("\\r?\\n");
                String cipher = null, keyB64 = null;
                for (String ln : lines) {
                    String s = ln.trim();
                    if (s.isEmpty())
                        continue;
                    if (cipher == null)
                        cipher = s;
                    else {
                        keyB64 = s;
                        break;
                    }
                }
                if (cipher == null || keyB64 == null) {
                    outputArea.setText("AES decrypt requires two lines:\n<cipherBase64>\n<keyBase64>");
                    return;
                }
                outputArea.setText(CryptoApp.aesDecryptFromBundle(cipher, keyB64));
            }
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void copyOutputToClipboard() {
        String text = outputArea.getText();
        if (text == null || text.isEmpty())
            return;
        StringSelection sel = new StringSelection(text);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(sel, null);
    }
}
