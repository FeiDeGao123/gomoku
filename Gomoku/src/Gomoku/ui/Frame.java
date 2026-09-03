package Gomoku.ui;

import Gomoku.logic.MainCheck;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Frame extends JFrame{
	private MainCheck ck;
	private int currColor;
	private boolean gameOver;
	private final int SQ = 52;
	private final int EDGE = 21;
	private final int radius = 20;
	
	public Frame() {
		ck = new MainCheck();
		currColor = MainCheck.BLACK;
		gameOver = false;
		this.setTitle("ÍøÂçÎå×ÓÆå");
		this.setSize(800, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.add(new Grid());
	}
	
	private class Grid extends JPanel {
		public Grid() {
			this.setBackground(new Color(222, 184, 135));
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
							String winner = currColor == ck.BLACK ? "ºÚÆå" : "°×Æå";
							JOptionPane.showMessageDialog(Frame.this, "¹§Ï²£¬" + winner + " »ñÊ¤£¡");
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
			for (int i = 0; i < ck.SIZE; i++) {
				g.drawLine(EDGE, EDGE + i * SQ, EDGE + (ck.SIZE - 1) * SQ, EDGE + i * SQ);
				g.drawLine(EDGE + i * SQ, EDGE, EDGE + i * SQ, EDGE + (ck.SIZE - 1) * SQ);
			}
			int grid[][] = ck.getGrid();
			for (int i = 0; i < ck.SIZE; i++) {
				for (int j = 0; j < ck.SIZE; j++) {
					if (grid[i][j] != 0) {
						int px = EDGE + i * SQ - radius;
						int py = EDGE + j * SQ - radius;
						if (grid[i][j] == ck.BLACK) {
							g.setColor(Color.BLACK);
						}
						else g.setColor(Color.WHITE);
						g.fillOval(px, py, radius * 2, radius * 2);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        Frame frame = new Frame();
	        frame.setVisible(true);
	    });
	}
}