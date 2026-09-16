package gomoku.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import gomoku.dao.user;
import gomoku.dao.DataProcessing;

public class RankDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    public RankDialog(Frame parent, DataProcessing dp) {
        super(parent, "天梯段位排行榜 TOP 50", true);
        this.setSize(560, 420);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        String[] columns = {"排名", "玩家昵称", "积分", "胜场", "负场", "胜率"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        List<user> rankList = dp.getTopRankings(50);
        int rank = 1;
        for (user u : rankList) {
            int total = u.getWinCnt() + u.getLoseCnt();
            String winRate = total == 0 ? "0.0%" : String.format("%.1f%%", (u.getWinCnt() * 100.0 / total));
            String rankStr = String.valueOf(rank);
            tableModel.addRow(new Object[]{
                rankStr,
                u.getName(),
                u.getScore() + " 分",
                u.getWinCnt(),
                u.getLoseCnt(),
                winRate
            });
            rank++;
        }
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(closeBtn);
        this.add(btnPanel, BorderLayout.SOUTH);
    }
}