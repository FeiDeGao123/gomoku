package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.network.IClient;
import gomoku.logic.GameCheck;

public class MenuPanel extends JPanel {
	private MainFrame pf;
	private IClient client;

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
		        if (pf == null) return;
		        String[] options = {"执黑 (先手)", "执白 (后手)"};
		        int choice = JOptionPane.showOptionDialog(
		            pf,
		            "请选择你的执棋方：",
		            "人机对战",
		            JOptionPane.DEFAULT_OPTION,
		            JOptionPane.QUESTION_MESSAGE,
		            null,
		            options,
		            options[0]
		        );
		        if (choice == 0) {
		            pf.startPveGame(GameCheck.BLACK);
		        } else if (choice == 1) {
		            pf.startPveGame(GameCheck.WHITE);
		        }
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

	public MenuPanel(MainFrame pf, IClient client) {
		this();
		this.pf = pf;
		this.client = client;
	}
}