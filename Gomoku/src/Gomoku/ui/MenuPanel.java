package Gomoku.ui;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel{
	private Frame pf;
	public MenuPanel(Frame pf) {
		this.pf = pf;
		this.setBackground(new Color(222, 184, 135));
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(15, 0, 15, 0);
		gbc.gridx = 0;
		JLabel title = new JLabel("网络五子棋");
		title.setFont(new Font("微软雅黑", Font.BOLD, 48));
		gbc.gridy = 0;
	    this.add(title, gbc);
	    JButton pveBtn = new JButton("人机对战");
        pveBtn.setPreferredSize(new Dimension(220, 50));
        pveBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        pveBtn.setFocusPainted(false);
        pveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(pf, "人机对战暂未开放！");
        });
        gbc.gridy = 1;
        this.add(pveBtn, gbc);
        JButton pvpLocalBtn = new JButton("多人单机");
        pvpLocalBtn.setPreferredSize(new Dimension(220, 50));
        pvpLocalBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        pvpLocalBtn.setFocusPainted(false);
        pvpLocalBtn.addActionListener(e -> {
        	pf.startLocalGame();
        });
        gbc.gridy = 2;
        this.add(pvpLocalBtn, gbc);
        JButton pvpOnlineBtn = new JButton("多人联机");
        pvpOnlineBtn.setPreferredSize(new Dimension(220, 50));
        pvpOnlineBtn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        pvpOnlineBtn.setFocusPainted(false);
        pvpOnlineBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(pf, "多人联机暂未开放！");
        });
        gbc.gridy = 3;
        this.add(pvpOnlineBtn, gbc);
	}
}
