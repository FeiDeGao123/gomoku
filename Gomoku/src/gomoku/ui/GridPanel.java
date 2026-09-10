package gomoku.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gomoku.logic.GameCheck;
import gomoku.network.IClient;
import gomoku.network.IListener;
import gomoku.logic.GameAI;

public class GridPanel extends JPanel implements IListener {
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
						} else {
							currColor ^= 3;
							String nextRole = (currColor == GameCheck.BLACK) ? "黑棋" : "白棋";
						    mf.updateStatus("[单机模式] 轮到 " + nextRole + " 落子", true);
						}
					}
				} else if (mode == ONLINE) {
					if (currColor != myColor) {
						JOptionPane.showMessageDialog(mf, "还没轮到你落子，请等待对手！");
						return;
					}
					if (gc.checkPush(x, y, myColor)) {
						preX = x;
						preY = y;
						repaint();
						client.sendPush(x, y);
						if (gc.checkEnd(x, y, myColor)) {
							gameOver = true;
							JOptionPane.showMessageDialog(mf, "恭喜，你战胜了对手！");
						} else {
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
				            JOptionPane.showMessageDialog(mf, "恭喜，你赢了！");
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
				                    } else {
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
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < GameCheck.SIZE; i++) {
			g2d.drawLine(EDGE, EDGE + i * SQ, EDGE + (GameCheck.SIZE - 1) * SQ, EDGE + i * SQ);
			g2d.drawLine(EDGE + i * SQ, EDGE, EDGE + i * SQ, EDGE + (GameCheck.SIZE - 1) * SQ);
		}
		
		int stars[][] = {{7, 7}, {3, 3}, {11, 3}, {3, 11}, {11, 11}};
		for (int[] star : stars) {
			int cx = EDGE + star[0] * SQ - DOT;
			int cy = EDGE + star[1] * SQ - DOT;
			g2d.fillOval(cx, cy, DOT * 2, DOT * 2);
		}
		
		int grid[][] = gc.getGrid();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0) {
					int px = EDGE + i * SQ - RADIUS;
					int py = EDGE + j * SQ - RADIUS;
					if (grid[i][j] == GameCheck.BLACK) {
						g2d.setColor(Color.BLACK);
					}
					else g2d.setColor(Color.WHITE);
					g2d.fillOval(px, py, RADIUS * 2, RADIUS * 2);
				}
			}
		}
		if (preX != -1 && preY != -1) {
			g2d.setColor(new Color(230, 0, 0));
			int cx = EDGE + preX * SQ - DOT;
			int cy = EDGE + preY * SQ - DOT;
			g2d.fillOval(cx, cy, DOT * 2, DOT * 2);
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
	            mf.updateStatus("[人机对战 - 你执白棋] 电脑思考中...", false);
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
			repaint();
			if (gc.checkEnd(x, y, oppColor)) {
				gameOver = true;
				JOptionPane.showMessageDialog(mf, "对手获胜！");
			} else {
				currColor ^= 3;
				String role = (myColor == GameCheck.BLACK) ? "黑棋" : "白棋";
			    mf.updateStatus("[联机对战 - 你执" + role + "] 你的回合", false);
			}
		}
	}

	public void otherQuit() {
		mf.updateStatus("[联机对战] 对手逃跑！", false);
		if (matchDialog != null && matchDialog.isShowing()) {
			matchDialog.dispose();
		}
		gameOver = true;
		JOptionPane.showMessageDialog(mf, "对手中途退出！");
	}
	
	public int getMode() {
		return this.mode;
	}
	
	public boolean getOver() {
		return this.gameOver;
	}
}