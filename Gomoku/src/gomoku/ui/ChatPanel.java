package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.network.IClient;

public class ChatPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextArea msgArea;
	private JTextField inField;
	private JButton sendBtn;
	private IClient client;
	private MainFrame mf;
	
	public ChatPanel(MainFrame mf, IClient client) {
		this.mf = mf;
		this.client = client;
		this.setPreferredSize(new Dimension(200, 0));
		this.setLayout(new BorderLayout(5, 5));
		this.setBackground(new Color(215, 175, 130));
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(160, 110, 70)),
				BorderFactory.createEmptyBorder(8, 8, 8, 8)
		));
		JLabel titleLabel = new JLabel("ÁÄÌìÊÒ", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 14));
		titleLabel.setForeground(new Color(70, 30, 10));
		this.add(titleLabel, BorderLayout.NORTH);
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		msgArea.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		msgArea.setBackground(new Color(250, 245, 235));
		JScrollPane scrollPane = new JScrollPane(msgArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(175, 135, 95)));
		this.add(scrollPane, BorderLayout.CENTER);
		JPanel bottomPanel = new JPanel(new BorderLayout(4, 0));
		bottomPanel.setOpaque(false);
		inField = new JTextField();
		inField.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 12));
		sendBtn = new JButton("·¢ËÍ");
		sendBtn.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 12));
		sendBtn.setFocusPainted(false);
		sendBtn.setBackground(new Color(90, 52, 28));
		sendBtn.setForeground(new Color(250, 240, 230));

		ActionListener sendAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		};
		sendBtn.addActionListener(sendAction);
		inField.addActionListener(sendAction);
		bottomPanel.add(inField, BorderLayout.CENTER);
		bottomPanel.add(sendBtn, BorderLayout.EAST);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void sendMessage() {
		String text = inField.getText().trim();
		if (text.isEmpty()) return;
		if (mf.getGridPanel() != null && mf.getGridPanel().getMode() == GridPanel.ONLINE) {
			client.sendChat(text);
			String myName = (mf.getCurrUser() != null) ? mf.getCurrUser().getName() : "ÎÒ";
			appendMessage(myName + "(ÎÒ)", text);
		} else {
			appendMessage("ÎÒ", text);
		}
		inField.setText("");
	}

	public void appendMessage(String sender, String text) {
		msgArea.append(sender + "£º\n  " + text + "\n");
		msgArea.setCaretPosition(msgArea.getDocument().getLength()); // ×Ô¶¯¹ö¶¯µ½µ×²¿
	}

	public void clear() {
		if (msgArea != null) {
			msgArea.setText("");
		}
	}
}