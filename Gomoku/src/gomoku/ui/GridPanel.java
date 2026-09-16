package gomoku.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gomoku.logic.GameCheck;
import gomoku.network.IClient;
import gomoku.network.IListener;
import gomoku.logic.GameAI;

public class GridPanel extends JPanel implements IListener {
	private static final long serialVersionUID = 1L;
	public static final int LOCAL = 1;
	public static final int ONLINE = 2;
	public static final int PVE = 3;
	
	private GameAI ai = new GameAI();
	private GameCheck gc;
	private MainFrame mf;
	private IClient client;
	private int mode = LOCAL;
	private int myColor;
	private int currColor;
	private boolean gameOver;
	private JDialog matchDialog;
	private int preX = -1;
	private int preY = -1;
	private StringBuilder stepsRecord = new StringBuilder();
	private final int SQ = 52;
	private final int EDGE = 21;
	private final int RADIUS = 20;
	private final int DOT = 5;
	
	public GridPanel(MainFrame mf, GameCheck gc, IClient client) {
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
						preX = x;
						preY = y;
						repaint();
						if (gc.checkEnd(x, y, currColor)) {
							gameOver = true;
							String winner = currColor == GameCheck.BLACK ? "黑棋" : "白棋";
							JOptionPane.showMessageDialog(mf, "恭喜，" + winner + " 获胜！");
						} 
						else if (gc.allPushed()) {
							gameOver = true;
							JOptionPane.showMessageDialog(mf, "棋盘已下满，和棋平局");
						}
						else {
							currColor ^= 3;
							String nextRole = (currColor == GameCheck.BLACK) ? "黑棋" : "白棋";
						    mf.updateStatus("[单机模式] 轮到 " + nextRole + " 落子", true);
						}
					}
				} 
				else if (mode == ONLINE) {
					if (currColor != myColor) {
						JOptionPane.showMessageDialog(mf, "还没轮到你落子，请等待对手！");
						return;
					}
					if (gc.checkPush(x, y, myColor)) {
						preX = x;
						preY = y;
						stepsRecord.append(String.format("%X%X", x, y));
						repaint();
						client.sendPush(x, y);
						if (gc.checkEnd(x, y, myColor)) {
							gameOver = true;
							mf.settleMatch(true, stepsRecord.toString());
							JOptionPane.showMessageDialog(mf, "恭喜，你战胜了对手！");
						} 
						else if (gc.allPushed()) {
							gameOver = true;
							JOptionPane.showMessageDialog(mf, "棋盘已下满，和棋平局");
						}
						else {
							currColor ^= 3;
							String role = (myColor == GameCheck.BLACK) ? "黑棋" : "白棋";
						    mf.updateStatus("[联机对战 - 你执" + role + "] 等待对手", false);
						}
					}
				}
				else if (mode == PVE) {
					if (currColor != myColor) return;
					if (gc.checkPush(x, y, myColor)) {
						preX = x;
						preY = y;
						repaint();
						if (gc.checkEnd(x, y, myColor)) {
							gameOver = true;
							mf.updateStatus("[人机对战] 你赢了！", true);
							JOptionPane.showMessageDialog(mf, "恭喜，你战胜了电脑！");
							return;
						}
						else if (gc.allPushed()) {
							gameOver = true;
							mf.updateStatus("[人机对战] 双方和棋！", true);
							JOptionPane.showMessageDialog(mf, "棋盘已下满，和棋平局");
							return;
						}
						currColor ^= 3;
						int aiColor = myColor ^ 3;
						mf.updateStatus("[人机对战] 电脑思考中...", false);
						Timer timer = new Timer(120, new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								int[] pos = ai.findMove(gc, aiColor);
								if (pos != null) {
									gc.checkPush(pos[0], pos[1], aiColor);
									preX = pos[0];
									preY = pos[1];
									repaint();
									if (gc.checkEnd(pos[0], pos[1], aiColor)) {
										gameOver = true;
										mf.updateStatus("[人机对战] 电脑获胜！", true);
										JOptionPane.showMessageDialog(mf, "电脑获胜！下次加油！");
									}
									else if (gc.allPushed()) {
										gameOver = true;
										mf.updateStatus("[人机对战] 双方和棋！", true);
										JOptionPane.showMessageDialog(mf, "棋盘已下满，和棋平局");
									}
									else {
										currColor ^= 3;
										String role = (myColor == GameCheck.BLACK) ? "黑棋" : "白棋";
										mf.updateStatus("[人机对战 - 你执" + role + "] [轮到你了]", true);
									}
								}
							}
						});
						timer.setRepeats(false);
						timer.start();
					}
				}
			}
		});
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		GomokuPaint.drawGrid(g2d, EDGE, SQ, DOT);
		int grid[][] = gc.getGrid();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0) {
					int cx = EDGE + i * SQ;
					int cy = EDGE + j * SQ;
					GomokuPaint.drawPiece(g2d, cx, cy, RADIUS, grid[i][j] == GameCheck.BLACK);
				}
			}
		}
		if (preX != -1 && preY != -1) {
			int cx = EDGE + preX * SQ;
			int cy = EDGE + preY * SQ;
			GomokuPaint.drawDot(g2d, cx, cy, DOT);
		}
	}
	
	public void restart() {
		if (this.mode == PVE) {
			startPve(this.myColor);
			return;
		}
		preX = -1;
		preY = -1;
		this.mode = LOCAL;
		gc.reset();
        currColor = GameCheck.BLACK;
        gameOver = false;
        repaint();
        if (mf != null) {
            mf.updateStatus("[单机模式] 轮到 黑棋 落子", true);
        }
    }

	public void playerWait() {
		matchDialog = new JDialog(mf, "对局匹配中", true);
		matchDialog.setSize(320, 160);
		matchDialog.setLocationRelativeTo(mf);
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
		preX = -1;
		preY = -1;
		stepsRecord.setLength(0);
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
		String role = (color == GameCheck.BLACK) ? "黑棋(先手)" : "白棋(后手)";
		String turn = (myColor == GameCheck.BLACK) ? "你的回合" : "等待对手";
		mf.updateStatus("[联机对战 - 你执" + role + "] " + turn, false);
		JOptionPane.showMessageDialog(mf, "匹配成功！对局开始，你执: " + role);
	}

	public void startPve(int playerColor) {
		preX = -1;
		preY = -1;
		this.mode = PVE;
		this.myColor = playerColor;
		gc.reset();
		currColor = GameCheck.BLACK;
		gameOver = false;
		repaint();

		if (myColor == GameCheck.BLACK) {
			if (mf != null) {
				mf.updateStatus("[人机对战 - 你执黑棋] [轮到你了]", true);
			}
		} else {
			if (mf != null) {
				mf.updateStatus("[人机对战 - 你执白棋] 电脑先手思考中...", false);
			}
			Timer timer = new Timer(120, new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					int aiColor = GameCheck.BLACK;
					int[] pos = ai.findMove(gc, aiColor);
					if (pos != null) {
						gc.checkPush(pos[0], pos[1], aiColor);
						preX = pos[0];
						preY = pos[1];
						repaint();
						currColor = GameCheck.WHITE;
						if (mf != null) {
							mf.updateStatus("[人机对战 - 你执白棋] [轮到你了]", true);
						}
					}
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
	
	public void otherPush(int x, int y) {
		int oppColor = myColor ^ 3;
		if (gc.checkPush(x, y, oppColor)) {
			preX = x;
			preY = y;
			stepsRecord.append(String.format("%X%X", x, y));
			repaint();
			if (gc.checkEnd(x, y, oppColor)) {
				gameOver = true;
				mf.settleMatch(false, stepsRecord.toString());
				JOptionPane.showMessageDialog(mf, "对手获胜！");
			} 
			else if (gc.allPushed()) {
				gameOver = true;
				JOptionPane.showMessageDialog(mf, "棋盘已下满，和棋平局");
			}
			else {
				currColor ^= 3;
				String role = (myColor == GameCheck.BLACK) ? "黑棋" : "白棋";
			    mf.updateStatus("[联机对战 - 你执" + role + "] 你的回合", false);
			}
		}
	}

	public void otherQuit() {
		if (matchDialog != null && matchDialog.isShowing()) {
			matchDialog.dispose();
		}
		if (!gameOver) {
			gameOver = true;
			mf.updateStatus("[联机对战] 对手逃跑！", false);
			mf.settleMatch(true, stepsRecord.toString());
			JOptionPane.showMessageDialog(mf, "对手中途逃跑，你获得胜利！");
		}
	}
	
	public void undo() {
		if (gameOver) {
			JOptionPane.showMessageDialog(mf, "本局游戏已结束，无法悔棋！");
			return;
		}
		if (mode == ONLINE) {
			JOptionPane.showMessageDialog(mf, "联机对战暂不支持悔棋！");
			return;
		}
		if (mode == LOCAL) {
			if (gc.stkSize() == 0) {
				JOptionPane.showMessageDialog(mf, "棋盘上还没有落子，无法悔棋！");
				return;
			}
			gc.stkPop();
			currColor ^= 3;
			int[] last = gc.stkTop();
			if (last != null) {
				preX = last[0];
				preY = last[1];
			} else {
				preX = -1;
				preY = -1;
			}
			repaint();
			String role = (currColor == GameCheck.BLACK) ? "黑棋" : "白棋";
			mf.updateStatus("[单机模式] 轮到 " + role + " 落子", true);
		} 
		else if (mode == PVE) {
			if (gc.stkSize() < 2) {
				JOptionPane.showMessageDialog(mf, "步数不足，无法悔棋！");
				return;
			}
			gc.stkPop();
			gc.stkPop();
			currColor = myColor;
			int[] last = gc.stkTop();
			if (last != null) {
				preX = last[0];
				preY = last[1];
			} else {
				preX = -1;
				preY = -1;
			}
			repaint();
			String role = (myColor == GameCheck.BLACK) ? "黑棋" : "白棋";
			mf.updateStatus("[人机对战 - 你执" + role + "] [轮到你了]", true);
		}
	}

	public void giveUp() {
		if (gameOver) {
			JOptionPane.showMessageDialog(mf, "游戏已结束！");
			return;
		}
		int choice = JOptionPane.showConfirmDialog(mf, "确认认输吗？", "认输确认", JOptionPane.YES_NO_OPTION);
		if (choice != JOptionPane.YES_OPTION) return;

		gameOver = true;
		if (mode == LOCAL) {
			String loser = (currColor == GameCheck.BLACK) ? "黑棋" : "白棋";
			String winner = (currColor == GameCheck.BLACK) ? "白棋" : "黑棋";
			JOptionPane.showMessageDialog(mf, loser + " 认输，恭喜 " + winner + " 获胜！");
			mf.updateStatus("[单机模式] " + winner + " 获胜！", true);
		} else if (mode == PVE) {
			JOptionPane.showMessageDialog(mf, "你已认输，电脑获胜！");
			mf.updateStatus("[人机对战] 电脑获胜！", true);
		} else if (mode == ONLINE) {
			mf.settleMatch(false, stepsRecord.toString());
			client.disconnect();
			JOptionPane.showMessageDialog(mf, "你已认输，本局判负！");
			mf.updateStatus("[联机对战] 你已认输！", true);
		}
	}
	
	public int getMode() {
		return this.mode;
	}
	
	public boolean getOver() {
		return this.gameOver;
	}
}