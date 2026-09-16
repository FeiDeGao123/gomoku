package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.dao.user;
import gomoku.logic.GameCheck;
import gomoku.network.IClient;

public class MenuPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private MainFrame pf;
	private JLabel userLabel;
	private JButton userBtn;

	public MenuPanel() {
		this(null, null);
	}

	public MenuPanel(MainFrame pf, IClient client) {
		this.pf = pf;
		this.setBackground(new Color(222, 184, 135));
		this.setLayout(new BorderLayout());
		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
		topBar.setOpaque(false);
		userLabel = new JLabel("状态: 未登录");
		userLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
		userLabel.setForeground(new Color(80, 40, 0));
		userBtn = new JButton("登录 / 注册");
		userBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
		userBtn.setFocusPainted(false);
		userBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleUserAction();
			}
		});
		topBar.add(userLabel);
		topBar.add(userBtn);
		this.add(topBar, BorderLayout.NORTH);
		JPanel centerPanel = new JPanel(new GridBagLayout());
		centerPanel.setOpaque(false);
		JLabel title = new JLabel("网 络 五 子 棋");
		title.setFont(new Font("微软雅黑", Font.BOLD, 48));
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.insets = new Insets(10, 0, 20, 0);
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 0;
		centerPanel.add(title, gbcTitle);
		JButton pveBtn = new JButton("人机对战");
		pveBtn.setPreferredSize(new Dimension(220, 48));
		pveBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pveBtn.setFocusPainted(false);
		pveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf == null) return;
				String[] options = {"执黑 (先手)", "执白 (后手)"};
				int choice = JOptionPane.showOptionDialog(
					pf, "请选择你的执棋方：", "人机对战设置",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]
				);
				if (choice == 0) {
					pf.startPveGame(GameCheck.BLACK);
				} else if (choice == 1) {
					pf.startPveGame(GameCheck.WHITE);
				}
			}
		});
		GridBagConstraints gbcPve = new GridBagConstraints();
		gbcPve.insets = new Insets(10, 0, 10, 0);
		gbcPve.gridx = 0;
		gbcPve.gridy = 1;
		centerPanel.add(pveBtn, gbcPve);
		JButton pvpLocalBtn = new JButton("单机对战");
		pvpLocalBtn.setPreferredSize(new Dimension(220, 48));
		pvpLocalBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pvpLocalBtn.setFocusPainted(false);
		pvpLocalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf != null) pf.startLocalGame();
			}
		});
		GridBagConstraints gbcLocal = new GridBagConstraints();
		gbcLocal.insets = new Insets(10, 0, 10, 0);
		gbcLocal.gridx = 0;
		gbcLocal.gridy = 2;
		centerPanel.add(pvpLocalBtn, gbcLocal);
		JButton pvpOnlineBtn = new JButton("联机对战");
		pvpOnlineBtn.setPreferredSize(new Dimension(220, 48));
		pvpOnlineBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pvpOnlineBtn.setFocusPainted(false);
		pvpOnlineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf != null && pf.getCurrUser() == null) {
					JOptionPane.showMessageDialog(pf, "联机对战需要登录账号！");
					handleUserAction();
					return;
				}
				if (client != null) client.sendConnect("124.223.22.121", 1145);
			}
		});
		GridBagConstraints gbcOnline = new GridBagConstraints();
		gbcOnline.insets = new Insets(10, 0, 10, 0);
		gbcOnline.gridx = 0;
		gbcOnline.gridy = 3;
		centerPanel.add(pvpOnlineBtn, gbcOnline);
		this.add(centerPanel, BorderLayout.CENTER);
		JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
		bottomBar.setOpaque(false);
		JButton historyBtn = new JButton("历史对局");
		historyBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
		historyBtn.setFocusPainted(false);
		historyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf == null || pf.getCurrUser() == null) {
					JOptionPane.showMessageDialog(pf, "请先在右上角登录账号以查看个人历史对局！");
					return;
				}
				HistoryDialog historyDialog = new HistoryDialog(pf, pf.getCurrUser().getID(), pf.getDataProcessing());
				historyDialog.setVisible(true);
			}
		});
		bottomBar.add(historyBtn);
		JButton rankBtn = new JButton("排行榜");
		rankBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
		rankBtn.setFocusPainted(false);
		rankBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf == null) return;
				RankDialog rankDialog = new RankDialog(pf, pf.getDataProcessing());
				rankDialog.setVisible(true);
			}
		});
		bottomBar.add(rankBtn);
		this.add(bottomBar, BorderLayout.SOUTH);
	}

	private void handleUserAction() {
		if (pf == null) return;
		if (pf.getCurrUser() == null) {
			LoginDialog dialog = new LoginDialog(pf);
			user u = dialog.showDialog();
			if (u != null) {
				pf.setCurrUser(u);
			}
		} else {
			int choice = JOptionPane.showConfirmDialog(pf, "确认注销当前账号？", "注销提示", JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				pf.setCurrUser(null);
			}
		}
	}

	public void updateUserInfo(user u) {
		if (u == null) {
			userLabel.setText("状态: 未登录");
			userBtn.setText("登录 / 注册");
		} else {
			userLabel.setText(u.getName() + " [分: " + u.getScore() + " | 胜: " + u.getWinCnt() + " 负: " + u.getLoseCnt() + "]");
			userBtn.setText("注销");
		}
	}
}