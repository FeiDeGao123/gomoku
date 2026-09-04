package Gomoku.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Gomoku.logic.MainCheck;

public class GridPanel extends JPanel {
	private MainCheck ck;
	private int currColor;
	private boolean gameOver;
	private final int SQ = 52;
	private final int EDGE = 21;
	private final int radius = 20;
	private Frame pf;
	
	public GridPanel(Frame pf) {
		this.pf = pf;
		this.setBackground(new Color(222, 184, 135));
		restart();
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (gameOver == true) return;
				int px = e.getX();
				int py = e.getY();
				int x = Math.round((float)(px - EDGE) / SQ);
				int y = Math.round((float)(py - EDGE) / SQ);
				if (ck.checkPush(x, y, currColor)) {
					repaint();
					if (ck.checkEnd(x, y, currColor)) {
						gameOver = true;
						String winner = currColor == MainCheck.BLACK ? "ºÚÆå" : "°×Æå";
						JOptionPane.showMessageDialog(pf, "¹§Ï²£¬" + winner + " »ñÊ¤£¡");
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
		for (int i = 0; i < MainCheck.SIZE; i++) {
			g.drawLine(EDGE, EDGE + i * SQ, EDGE + (MainCheck.SIZE - 1) * SQ, EDGE + i * SQ);
			g.drawLine(EDGE + i * SQ, EDGE, EDGE + i * SQ, EDGE + (MainCheck.SIZE - 1) * SQ);
		}
		int grid[][] = ck.getGrid();
		for (int i = 0; i < MainCheck.SIZE; i++) {
			for (int j = 0; j < MainCheck.SIZE; j++) {
				if (grid[i][j] != 0) {
					int px = EDGE + i * SQ - radius;
					int py = EDGE + j * SQ - radius;
					if (grid[i][j] == MainCheck.BLACK) {
						g.setColor(Color.BLACK);
					}
					else g.setColor(Color.WHITE);
					g.fillOval(px, py, radius * 2, radius * 2);
				}
			}
		}
	}
	
	public void restart() {
        ck = new MainCheck();
        currColor = MainCheck.BLACK;
        gameOver = false;
        repaint();
    }
}

