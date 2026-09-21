package gomoku.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import gomoku.dao.MatchRecord;
import gomoku.logic.GameCheck;

/**
 * 棋谱单步复盘与文件导出对话框
 * 继承自 JDialog，提供历史对局重放与本地导出
 */
public class ReplayDialog extends JDialog {
	private static final long serialVersionUID = 1L;
    private List<int[]> stepsList = new ArrayList<>();
    private int currStep = 0;
    private ReplayPanel boardPanel;
    private JLabel infoLabel;
    private MatchRecord record;
    private final int SQ = 40;
    private final int EDGE = 25;
    private final int RADIUS = 16;
    private final int DOT = 4;

    /**
     * 构造方法，解码十六进制棋谱、初始化重放
     * @param parent 依赖的父级对话框
     * @param record 待复盘的对局实体数据传输对象
     */
    public ReplayDialog(Dialog parent, MatchRecord record) {
        super(parent, "棋谱复盘 - " + String.format("%08d", record.getDjh()) + " [" + record.getRes() + "]", true);
        this.record = record;
        this.setSize(640, 750);
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
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnPanel.setOpaque(false);
        JButton startBtn = new JButton("开局");
        JButton prevBtn = new JButton("上一步");
        JButton nextBtn = new JButton("下一步");
        JButton endBtn = new JButton("结束");
        JButton exportBtn = new JButton("导出棋谱txt");
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
        		currStep++; 
        		updateView();
        	}
        });
        endBtn.addActionListener(e -> { 
        	currStep = stepsList.size(); 
        	updateView(); 
        });
        exportBtn.setFont(new Font("微软雅黑", Font.BOLD, 12));
        exportBtn.setFocusPainted(false);
        exportBtn.addActionListener(e -> exportToFile());
        btnPanel.add(startBtn);
        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);
        btnPanel.add(endBtn);
        btnPanel.add(exportBtn);
        controlPanel.add(infoLabel, BorderLayout.NORTH);
        controlPanel.add(btnPanel, BorderLayout.SOUTH);
        this.add(controlPanel, BorderLayout.SOUTH);
        updateView();
    }

    /**
     * 十六进制棋谱解码
     * @param hex 数据库中存储的十六进制棋谱文本
     */
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

    /**
     * 刷新复盘状态与标签
     */
    private void updateView() {
        if (currStep == 0) {
            infoLabel.setText("初始空棋盘 (共 " + stepsList.size() + " 步)");
        } else {
            int[] last = stepsList.get(currStep - 1);
            String role = (last[2] == GameCheck.BLACK) ? "黑棋" : "白棋";
            infoLabel.setText("第 " + currStep + " / " + stepsList.size() + " 步 [" 
            		+ role + " 落子于 (第 " + (last[0] + 1) + " 列, 第 " + (last[1] + 1) + " 行)]");
        }
        boardPanel.repaint();
    }

    /**
     * 将对战棋谱导出 txt
     */
    private void exportToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存棋谱文本文件");
        fileChooser.setSelectedFile(new File("gomoku_match_" + String.format("%08d", record.getDjh()) + ".txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("文本文档 (*.txt)", "txt"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".txt");
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {
            writer.write("对局编号: " + String.format("%08d", record.getDjh()) + "\n");
            writer.write("对局战果: " + record.getRes() + "\n");
            writer.write("积分变动: " + (record.getChange() > 0 ? "+" : "") + record.getChange() + " 分 (赛后积分: " + record.getNextRating() + " 分)\n");
            writer.write("总计手数: " + record.getStepCount() + " 步\n");
            writer.write("对局时间: " + record.getMatchTime() + "\n");
            writer.write("步骤编码: " + record.getStep() + "\n");
            writer.write("落子明细:\n");
            for (int i = 0; i < stepsList.size(); i++) {
                int[] step = stepsList.get(i);
                String role = (step[2] == GameCheck.BLACK) ? "黑棋" : "白棋";
                int row = step[1] + 1;
                int col = step[0] + 1;
                writer.write("第 " + (i + 1) + " 步: " + role + " 落子于第 " + row + " 行, 第 " + col + " 列\n");
            }
            writer.flush();
            JOptionPane.showMessageDialog(this, "棋谱已成功导出至:\n" + fileToSave.getAbsolutePath(), "导出成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "导出棋谱失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 复盘棋盘绘图面板
     */
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