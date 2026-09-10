package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.logic.GameCheck;
import gomoku.network.IClient;
import gomoku.network.SocketClient;

public class MainFrame extends JFrame {
    private CardLayout cl;
    private JPanel jp;
    private MenuPanel mp;
    private GridPanel gp;
    private GameCheck gc;
    private IClient client;
    private JButton restartBtn;
    private JLabel status;
    
    public MainFrame() {
        this.setTitle("网络五子棋");
        this.setSize(800, 850);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.gc = new GameCheck();
        this.client = new SocketClient();
        cl = new CardLayout();
        jp = new JPanel(cl);
        mp = new MenuPanel(this, client);
        gp = new GridPanel(this, gc, client);
        client.setObj(gp);
        JPanel OptionBar = createBar();
        jp.add(mp, "MENU");
        jp.add(OptionBar, "GAME");
        this.add(jp);
        cl.show(jp, "MENU");
    }
    
    private JPanel createBar() {
    	JPanel option = new JPanel(new BorderLayout());
    	JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
    	bar.setBackground(new Color(205, 168, 120));
    	JButton backBtn = new JButton("返回");
    	backBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
    	backBtn.setFocusPainted(false);
    	backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gp.getMode() == GridPanel.ONLINE && !gp.getOver()) {
	    			int choice = JOptionPane.showConfirmDialog(MainFrame.this, 
	    					"当前正在联机对战中，退出将直接判负退出，是否确认退出？", 
	    					"退出警告", JOptionPane.YES_NO_OPTION);
	    		    if (choice != JOptionPane.YES_OPTION) return;
	    		}
	    		back();
			}
		});

    	restartBtn = new JButton("重新开始");
        restartBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        restartBtn.setFocusPainted(false);
        restartBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.restart();
			}
		});

        JButton settingBtn = new JButton("设置");
        settingBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        settingBtn.setFocusPainted(false);
        settingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this, "没做");
			}
		});

        bar.add(backBtn);
        bar.add(restartBtn);
        bar.add(settingBtn);
        status = new JLabel("准备中...");
        status.setFont(new Font("微软雅黑", Font.BOLD, 15));
        status.setForeground(new Color(60, 20, 0));
        bar.add(status);
        option.add(bar, BorderLayout.NORTH);
        option.add(gp, BorderLayout.CENTER);
        return option;
    }
    
    public void startLocalGame() {
    	gp.restart();
        cl.show(jp, "GAME");
    }

    public void startOnlineGame() {
        cl.show(jp, "GAME");
    }
    
    public void startPveGame(int playerColor) {
        gp.startPve(playerColor);
        cl.show(jp, "GAME");
    }
    
    public void back() {
    	client.disconnect();
    	gp.restart();
    	cl.show(jp, "MENU");
    }
    
    public void updateStatus(String text, boolean restartFlag) {
    	if (status != null) {
    		status.setText(text);
    	}
    	if (restartBtn != null) {
    		restartBtn.setEnabled(restartFlag);
    	}
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}