package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import gomoku.network.IClient;

public class ChatPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel titleLabel;
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
		titleLabel = new JLabel("ÁÄÌì¿ò", SwingConstants.CENTER);
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
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				adjustChatUI();
			}
		});
	}

	private void adjustChatUI() {
		int w = getWidth();
		if (w <= 0) return;
		float scale = Math.max(1.0f, Math.min(1.8f, w / 200.0f));
		if (titleLabel != null) {
			titleLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, Math.round(14 * scale)));
		}
		if (msgArea != null) {
			msgArea.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, Math.round(12 * scale)));
		}
		if (inField != null) {
			inField.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, Math.round(12 * scale)));
		}
		if (sendBtn != null) {
			sendBtn.setFont(new Font("Î¢ÈíÑÅºÚ", Font.BOLD, Math.round(12 * scale)));
			sendBtn.setPreferredSize(new Dimension(Math.round(60 * scale), Math.round(28 * scale)));
		}
		revalidate();
		repaint();
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
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
	}

	public void clear() {
		if (msgArea != null) {
			msgArea.setText("");
		}
	}
}