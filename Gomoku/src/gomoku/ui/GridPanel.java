package gomoku.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gomoku.logic.GameCheck;

public class GridPanel extends JPanel {
	private GameCheck gc;
	private int currColor;
	private boolean gameOver;
	private final int SQ = 52;
	private final int EDGE = 21;
	private final int RADIUS = 20;
	private MainFrame mf;
	
	public GridPanel(MainFrame mf, GameCheck gc) {
		this.mf = mf;
		this.gc = gc;
		this.setBackground(new Color(222, 184, 135));
		restart();
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (gameOver == true) return;
				int px = e.getX();
				int py = e.getY();
				int x = Math.round((float)(px - EDGE) / SQ);
				int y = Math.round((float)(py - EDGE) / SQ);
				if (gc.checkPush(x, y, currColor)) {
					repaint();
					if (gc.checkEnd(x, y, currColor)) {
						gameOver = true;
						String winner = currColor == GameCheck.BLACK ? "ºÚÆå" : "°×Æå";
						JOptionPane.showMessageDialog(mf, "¹§Ï²£¬" + winner + " »ñÊ¤£¡");
					}
					else {
						currColor ^= 3;
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
		gc.reset();
        currColor = GameCheck.BLACK;
        gameOver = false;
        repaint();
    }
}

