package gomoku.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import gomoku.dao.MatchRecord;
import gomoku.dao.DataProcessing;

public class HistoryDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel tableModel;
    private List<MatchRecord> recordList;

    public HistoryDialog(Frame parent, int userId, DataProcessing dp) {
        super(parent, "个人对局历史", true);
        this.setSize(720, 420);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        recordList = dp.getHistoryByUserId(userId);
        String[] columns = {"对局编号", "结果", "积分变动", "赛后积分", "总手数", "对局时间"};
        tableModel = new DefaultTableModel(columns, 0) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
            	return false;
            }
        };
        for (MatchRecord r : recordList) {
            String changeStr = (r.getChange() > 0 ? "+" : "") + r.getChange() + " 分";
            tableModel.addRow(new Object[]{
            	String.format("%08d", r.getDjh()),
                r.getRes(),
                changeStr,
                r.getNextRating() + " 分",
                r.getStepCount() + " 步",
                r.getMatchTime()
            });
        }

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openReplay();
                }
            }
        });
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton viewBtn = new JButton("查看选中棋谱");
        viewBtn.setFont(new Font("微软雅黑", Font.BOLD, 13));
        viewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openReplay();
            }
        });
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());
        btnPanel.add(viewBtn);
        btnPanel.add(closeBtn);
        this.add(btnPanel, BorderLayout.SOUTH);
    }

    private void openReplay() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < recordList.size()) {
            MatchRecord selected = recordList.get(row);
            ReplayDialog replayDialog = new ReplayDialog(this, selected);
            replayDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "请先在列表中选中一场对局！");
        }
    }
}