package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import gomoku.network.IClient;

/**
 * 五子棋对局即时聊天面板
 */
public class ChatPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel titleLabel;
	private JTextArea msgArea;
	private JTextField inField;
	private JButton sendBtn;
	private IClient client;
	private MainFrame mf;
	
	/**
	 * 构造方法，初始化聊天面板布局、配色样式与事件监听
	 * @param mf 主窗口对象引用
	 * @param client 网络通信客户端引用
	 */
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
		titleLabel = new JLabel("聊天框", SwingConstants.CENTER);
		titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
		titleLabel.setForeground(new Color(70, 30, 10));
		this.add(titleLabel, BorderLayout.NORTH);
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		msgArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		msgArea.setBackground(new Color(250, 245, 235));
		JScrollPane scrollPane = new JScrollPane(msgArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(175, 135, 95)));
		this.add(scrollPane, BorderLayout.CENTER);
		JPanel bottomPanel = new JPanel(new BorderLayout(4, 0));
		bottomPanel.setOpaque(false);
		inField = new JTextField();
		inField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		sendBtn = new JButton("发送");
		sendBtn.setFont(new Font("微软雅黑", Font.BOLD, 12));
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

	/**
	 * 布局动态缩放
	 * 根据聊天面板当前实际宽度，动态计算倍率，按比例调整各控件字号与尺寸
	 */
	private void adjustChatUI() {
		int w = getWidth();
		if (w <= 0) return;
		float scale = Math.max(1.0f, Math.min(1.8f, w / 200.0f));
		if (titleLabel != null) {
			titleLabel.setFont(new Font("微软雅黑", Font.BOLD, Math.round(14 * scale)));
		}
		if (msgArea != null) {
			msgArea.setFont(new Font("微软雅黑", Font.PLAIN, Math.round(12 * scale)));
		}
		if (inField != null) {
			inField.setFont(new Font("微软雅黑", Font.PLAIN, Math.round(12 * scale)));
		}
		if (sendBtn != null) {
			sendBtn.setFont(new Font("微软雅黑", Font.BOLD, Math.round(12 * scale)));
			sendBtn.setPreferredSize(new Dimension(Math.round(60 * scale), Math.round(28 * scale)));
		}
		revalidate();
		repaint();
	}
	
	/**
	 * 消息发送业务处理
	 * 过滤空字符，若处于联机模式则通过 SocketClient 上报，并在本地追加回显
	 */
	private void sendMessage() {
		String text = inField.getText().trim();
		if (text.isEmpty()) return;
		if (mf.getGridPanel() != null && mf.getGridPanel().getMode() == GridPanel.ONLINE) {
			client.sendChat(text);
			String myName = (mf.getCurrUser() != null) ? mf.getCurrUser().getName() : "我";
			appendMessage(myName + "(我)", text);
		} else {
			appendMessage("我", text);
		}
		inField.setText("");
	}

	/**
	 * 向历史文本域中追加一条聊天记录并自动保持滚动条在最底部
	 * @param sender 发送者昵称
	 * @param text 聊天文本内容
	 */
	public void appendMessage(String sender, String text) {
		msgArea.append(sender + "：\n  " + text + "\n");
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
	}

	/**
	 * 清空历史聊天记录
	 */
	public void clear() {
		if (msgArea != null) {
			msgArea.setText("");
		}
	}
}