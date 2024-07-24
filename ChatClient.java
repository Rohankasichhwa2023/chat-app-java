import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;

public class ChatClient {
    JTextPane incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    String userName;

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.go();
    }

    public void go() {
        // Ask for user's name using input dialog
        userName = JOptionPane.showInputDialog(null, "Enter your name:");

        JFrame frame = new JFrame("Chat Client");
        JPanel mainPanel = new JPanel();
        incoming = new JTextPane();
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(30);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new SendButtonListener());

        // Chat history area
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.PAGE_AXIS));
        historyPanel.add(qScroller);

        // Input area
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
        inputPanel.add(outgoing);
        inputPanel.add(sendButton);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(historyPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setUpNetworking();

        IncomingReader inr = new IncomingReader();
        Thread readerThread = new Thread(inr);
        readerThread.start();
    }

    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                String message = userName + ": " + outgoing.getText();
                writer.println(message);
                writer.flush();
                appendMessage(message, true); // Append message for sender only
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    public class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    if (!message.startsWith(userName + ":")) { // Only append if the message is not from the sender
                        appendMessage(message, false); // Append message for receiver only
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void appendMessage(String message, boolean isSender) {
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, isSender ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(set, isSender ? Color.BLUE : Color.BLACK);

        int len = incoming.getDocument().getLength();
        try {
            incoming.getDocument().insertString(len, message + "\n", set);
            incoming.getStyledDocument().setParagraphAttributes(len, message.length(), set, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}