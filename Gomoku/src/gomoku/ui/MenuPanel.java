package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.network.GameClient;

public class MenuPanel extends JPanel {
	private MainFrame pf;
	private GameClient client;

	public MenuPanel() {
		this.setBackground(new Color(222, 184, 135));
		this.setLayout(new GridBagLayout());

		JLabel title = new JLabel("网 络 五 子 棋");
		title.setFont(new Font("微软雅黑", Font.BOLD, 48));
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.insets = new Insets(15, 0, 15, 0);
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 0;
		this.add(title, gbcTitle);

		JButton pveBtn = new JButton("人机对战");
		pveBtn.setPreferredSize(new Dimension(220, 50));
		pveBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pveBtn.setFocusPainted(false);
		pveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(pf, "人机对战暂未开放！");
			}
		});
		GridBagConstraints gbcPve = new GridBagConstraints();
		gbcPve.insets = new Insets(15, 0, 15, 0);
		gbcPve.gridx = 0;
		gbcPve.gridy = 1;
		this.add(pveBtn, gbcPve);

		JButton pvpLocalBtn = new JButton("单机对战");
		pvpLocalBtn.setPreferredSize(new Dimension(220, 50));
		pvpLocalBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pvpLocalBtn.setFocusPainted(false);
		pvpLocalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pf != null) pf.startLocalGame();
			}
		});
		GridBagConstraints gbcLocal = new GridBagConstraints();
		gbcLocal.insets = new Insets(15, 0, 15, 0);
		gbcLocal.gridx = 0;
		gbcLocal.gridy = 2;
		this.add(pvpLocalBtn, gbcLocal);

		JButton pvpOnlineBtn = new JButton("联机对战");
		pvpOnlineBtn.setPreferredSize(new Dimension(220, 50));
		pvpOnlineBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
		pvpOnlineBtn.setFocusPainted(false);
		pvpOnlineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client != null) client.sendConnect("124.223.22.121", 1145);
			}
		});
		GridBagConstraints gbcOnline = new GridBagConstraints();
		gbcOnline.insets = new Insets(15, 0, 15, 0);
		gbcOnline.gridx = 0;
		gbcOnline.gridy = 3;
		this.add(pvpOnlineBtn, gbcOnline);
	}

	public MenuPanel(MainFrame pf, GameClient client) {
		this();
		this.pf = pf;
		this.client = client;
	}
}