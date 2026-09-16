package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import gomoku.dao.MatchRecord;
import gomoku.logic.GameCheck;

public class ReplayDialog extends JDialog {
	private static final long serialVersionUID = 1L;
    private List<int[]> stepsList = new ArrayList<>();
    private int currStep = 0;
    private ReplayPanel boardPanel;
    private JLabel infoLabel;
    private final int SQ = 40;
    private final int EDGE = 25;
    private final int RADIUS = 16;
    private final int DOT = 4;

    public ReplayDialog(Dialog parent, MatchRecord record) {
        super(parent, "棋谱复盘 - " + String.format("%08d", record.getDjh()) + " [" + record.getRes() + "]", true);
        this.setSize(620, 720);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        parseHexSteps(record.getStep());
        boardPanel = new ReplayPanel();
        this.add(boardPanel, BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(new Color(205, 168, 120));
        infoLabel = new JLabel("准备就绪", SwingConstants.CENTER);
        infoLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setOpaque(false);
        JButton startBtn = new JButton("开局");
        JButton prevBtn = new JButton("上一步");
        JButton nextBtn = new JButton("下一步");
        JButton endBtn = new JButton("结束");
        startBtn.addActionListener(e -> {
        	currStep = 0;
        	updateView();
        });
        prevBtn.addActionListener(e -> {
        	if (currStep > 0) {
        		currStep--;
        		updateView();
        		} 
        });
        nextBtn.addActionListener(e -> {
        	if (currStep < stepsList.size()) {
        		currStep++; updateView();
        	}
        });
        endBtn.addActionListener(e -> { 
        	currStep = stepsList.size(); 
        	updateView(); 
        });
        btnPanel.add(startBtn);
        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);
        btnPanel.add(endBtn);
        controlPanel.add(infoLabel, BorderLayout.NORTH);
        controlPanel.add(btnPanel, BorderLayout.SOUTH);
        this.add(controlPanel, BorderLayout.SOUTH);
        updateView();
    }

    private void parseHexSteps(String hex) {
        if (hex == null || hex.trim().isEmpty()) return;
        hex = hex.trim().toUpperCase();
        for (int i = 0; i < hex.length(); i += 2) {
            int x = Character.digit(hex.charAt(i), 16);
            int y = Character.digit(hex.charAt(i + 1), 16);
            int color = ((i / 2) % 2 == 0) ? GameCheck.BLACK : GameCheck.WHITE;
            stepsList.add(new int[]{x, y, color});
        }
    }

    private void updateView() {
        if (currStep == 0) {
            infoLabel.setText("初始空棋盘 (共 " + stepsList.size() + " 步)");
        } else {
            int[] last = stepsList.get(currStep - 1);
            String role = (last[2] == GameCheck.BLACK) ? "黑棋" : "白棋";
            infoLabel.setText("第 " + currStep + " / " + stepsList.size() + " 步 [" 
            		+ role + " 落子于 (第 " + (last[1] + 1) + " 行, 第 " + (last[0] + 1) + " 列)]");
        }
        boardPanel.repaint();
    }

    private class ReplayPanel extends JPanel {
    	private static final long serialVersionUID = 1L;
        public ReplayPanel() {
            this.setBackground(new Color(222, 184, 135));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GomokuPaint.drawGrid(g2d, EDGE, SQ, DOT);
            int lastX = -1, lastY = -1;
            for (int k = 0; k < currStep; k++) {
                int[] step = stepsList.get(k);
                int cx = EDGE + step[0] * SQ;
                int cy = EDGE + step[1] * SQ;
                GomokuPaint.drawPiece(g2d, cx, cy, RADIUS, step[2] == GameCheck.BLACK);
                lastX = step[0];
                lastY = step[1];
            }
            if (lastX != -1 && lastY != -1) {
                int cx = EDGE + lastX * SQ;
                int cy = EDGE + lastY * SQ;
                GomokuPaint.drawDot(g2d, cx, cy, DOT);
            }
        }
    }
}