package gomoku.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gomoku.logic.GameCheck;
import gomoku.network.GameClient;
import gomoku.network.GameListener;

public class GridPanel extends JPanel implements GameListener {
	public static final int LOCAL = 1;
	public static final int ONLINE = 2;
	private GameCheck gc;
	private MainFrame mf;
	private GameClient client;
	private int mode = LOCAL;
	private int myColor;
	private int currColor;
	private boolean gameOver;
	private JDialog matchDialog;
	private final int SQ = 52;
	private final int EDGE = 21;
	private final int RADIUS = 20;
	private final int DOT = 5;
	
	public GridPanel(MainFrame mf, GameCheck gc, GameClient client) {
		this.mf = mf;
		this.gc = gc;
		this.client = client;
		this.setBackground(new Color(222, 184, 135));
		restart();
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (gameOver == true) return;
				int px = e.getX();
				int py = e.getY();
				int x = Math.round((float)(px - EDGE) / SQ);
				int y = Math.round((float)(py - EDGE) / SQ);

				if (mode == LOCAL) {
					if (gc.checkPush(x, y, currColor)) {
						repaint();
						if (gc.checkEnd(x, y, currColor)) {
							gameOver = true;
							String winner = currColor == GameCheck.BLACK ? "黑棋" : "白棋";
							JOptionPane.showMessageDialog(mf, "恭喜，" + winner + " 获胜！");
						} else {
							currColor ^= 3;
						}
					}
				} else if (mode == ONLINE) {
					if (currColor != myColor) {
						JOptionPane.showMessageDialog(mf, "还没轮到你落子，请等待对手！");
						return;
					}

					if (gc.checkPush(x, y, myColor)) {
						repaint();
						client.sendPush(x, y);

						if (gc.checkEnd(x, y, myColor)) {
							gameOver = true;
							JOptionPane.showMessageDialog(mf, "恭喜，你战胜了对手！");
						} else {
							currColor ^= 3;
						}
					}
				}
			}
		});
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		for (int i = 0; i < GameCheck.SIZE; i++) {
			g.drawLine(EDGE, EDGE + i * SQ, EDGE + (GameCheck.SIZE - 1) * SQ, EDGE + i * SQ);
			g.drawLine(EDGE + i * SQ, EDGE, EDGE + i * SQ, EDGE + (GameCheck.SIZE - 1) * SQ);
		}
		
		int stars[][] = {{7, 7}, {3, 3}, {11, 3}, {3, 11}, {11, 11}};
		for (int[] star : stars) {
			int cx = EDGE + star[0] * SQ - DOT;
			int cy = EDGE + star[1] * SQ - DOT;
			g.fillOval(cx, cy, DOT * 2, DOT * 2);
		}
		
		int grid[][] = gc.getGrid();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0) {
					int px = EDGE + i * SQ - RADIUS;
					int py = EDGE + j * SQ - RADIUS;
					if (grid[i][j] == GameCheck.BLACK) {
						g.setColor(Color.BLACK);
					}
					else g.setColor(Color.WHITE);
					g.fillOval(px, py, RADIUS * 2, RADIUS * 2);
				}
			}
		}
	}
	
	public void restart() {
		this.mode = LOCAL;
		gc.reset();
        currColor = GameCheck.BLACK;
        gameOver = false;
        repaint();
    }

	public void playerWait() {
		matchDialog = new JDialog(mf, "对局匹配中", true);
		matchDialog.setSize(320, 160);
		matchDialog.setLocationRelativeTo(mf); // 居中显示
		matchDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout(10, 15));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
		JLabel label = new JLabel("正在匹配，请稍候...", SwingConstants.CENTER);
		label.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		JButton cancelBtn = new JButton("取消匹配");
		cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
		cancelBtn.setFocusPainted(false);
		cancelBtn.addActionListener(e -> {
			client.disconnect();
			matchDialog.dispose();
		});
		matchDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				client.disconnect();
			}
		});
		panel.add(label, BorderLayout.CENTER);
		panel.add(cancelBtn, BorderLayout.SOUTH);
		matchDialog.add(panel);
		matchDialog.setVisible(true);
	}

	public void gameStart(int color) {
		if (matchDialog != null && matchDialog.isShowing()) {
			matchDialog.dispose();
		}
		this.mode = ONLINE;
		this.myColor = color;
		this.currColor = GameCheck.BLACK;
		gc.reset();
		this.gameOver = false;
		repaint();
		mf.startOnlineGame();
		String role = (color == GameCheck.BLACK) ? "黑棋 (先手)" : "白棋 (后手)";
		JOptionPane.showMessageDialog(mf, "匹配成功！对局开始，你执: " + role);
	}

	public void otherPush(int x, int y) {
		int oppColor = myColor ^ 3;
		if (gc.checkPush(x, y, oppColor)) {
			repaint();
			if (gc.checkEnd(x, y, oppColor)) {
				gameOver = true;
				JOptionPane.showMessageDialog(mf, "对手五连珠获胜，下次再战！");
			} else {
				currColor ^= 3;
			}
		}
	}

	public void otherQuit() {
		if (matchDialog != null && matchDialog.isShowing()) {
			matchDialog.dispose();
		}
		gameOver = true;
		JOptionPane.showMessageDialog(mf, "对手中途退出了游戏！");
	}
}