package gomoku.ui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import gomoku.dao.User;
import gomoku.dao.DataProcessing;
import gomoku.logic.GameCheck;
import gomoku.network.IClient;
import gomoku.network.SocketClient;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
    private static float scaleFactor = 1.0f;
    private CardLayout cl;
    private JPanel jp;
    private MenuPanel mp;
    private GridPanel gp;
    private ChatPanel chatPanel;
    private GameCheck gc;
    private IClient client;
    private JButton rstBtn;
    private JButton undoBtn;
    private JButton giveUpBtn;
    private JLabel status;
    private User currUser;
    private DataProcessing dp = new DataProcessing();
    
    public MainFrame() {
        this.setTitle("网络五子棋");
        int frameW = (int) (920 * scaleFactor);
        int frameH = (int) (850 * scaleFactor);
        this.setSize(frameW, frameH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setMinimumSize(new Dimension((int) (750 * scaleFactor), (int) (700 * scaleFactor)));
        this.gc = new GameCheck();
        this.client = new SocketClient();
        cl = new CardLayout();
        jp = new JPanel(cl);
        mp = new MenuPanel(this, client);
        gp = new GridPanel(this, gc, client);
        chatPanel = new ChatPanel(this, client);
        client.setObj(gp);
        JPanel OptionBar = createBar();
        jp.add(mp, "MENU");
        jp.add(OptionBar, "GAME");
        this.add(jp);
        cl.show(jp, "MENU");
    }

    public User getCurrUser() {
    	return currUser;
    }
    
    public DataProcessing getDataProcessing() {
    	return dp;
    }

    public GridPanel getGridPanel() {
        return gp;
    }

    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    public void setCurrUser(User u) {
        this.currUser = u;
        if (mp != null) {
            mp.updateUserInfo(currUser);
        }
    }

    public void settleMatch(boolean isWin, String hexSteps) {
        if (currUser != null) {
            dp.settleMatch(currUser.getID(), isWin, hexSteps);
            this.currUser = dp.getUserById(currUser.getID());
            if (mp != null) {
                mp.updateUserInfo(currUser);
            }
        }
    }
    
    private JPanel createBar() {
    	JPanel option = new JPanel(new BorderLayout());
    	JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
    	bar.setBackground(new Color(205, 168, 120));
    	JButton backBtn = new JButton("返回");
    	backBtn.setFont(new Font("微软雅黑", Font.BOLD, (int) (14 * scaleFactor)));
    	backBtn.setFocusPainted(false);
    	backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gp.getMode() == GridPanel.ONLINE && !gp.getOver()) {
	    			int choice = JOptionPane.showConfirmDialog(MainFrame.this, 
	    					"当前正在联机对战中，退出将直接判负退出，是否确认退出？", 
	    					"退出警告", JOptionPane.YES_NO_OPTION);
	    		    if (choice != JOptionPane.YES_OPTION) return;
	    		    settleMatch(false, "");
	    		}
	    		back();
			}
		});
    	rstBtn = new JButton("重新开始");
        rstBtn.setFont(new Font("微软雅黑", Font.BOLD, (int) (14 * scaleFactor)));
        rstBtn.setFocusPainted(false);
        rstBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.restart();
			}
		});
        undoBtn = new JButton("悔棋");
        undoBtn.setFont(new Font("微软雅黑", Font.BOLD, (int) (14 * scaleFactor)));
        undoBtn.setFocusPainted(false);
        undoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.undo();
			}
		});
        giveUpBtn = new JButton("认输");
        giveUpBtn.setFont(new Font("微软雅黑", Font.BOLD, (int) (14 * scaleFactor)));
        giveUpBtn.setFocusPainted(false);
        giveUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gp.giveUp();
			}
		});
        JButton settingBtn = new JButton("设置");
        settingBtn.setFont(new Font("微软雅黑", Font.BOLD, (int) (14 * scaleFactor)));
        settingBtn.setFocusPainted(false);
        settingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingDialog settingDialog = new SettingDialog(MainFrame.this);
				settingDialog.setVisible(true);
			}
		});
        bar.add(backBtn);
        bar.add(rstBtn);
        bar.add(undoBtn);
        bar.add(giveUpBtn);
        bar.add(settingBtn);
        status = new JLabel("准备中...");
        status.setFont(new Font("微软雅黑", Font.BOLD, (int) (15 * scaleFactor)));
        status.setForeground(new Color(60, 20, 0));
        bar.add(status);

        option.add(bar, BorderLayout.NORTH);
        option.add(gp, BorderLayout.CENTER);
        option.add(chatPanel, BorderLayout.EAST);
        return option;
    }
    
    public void startLocalGame() {
    	gp.restart();
    	if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }

    public void startOnlineGame() {
    	if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }
    
    public void startPveGame(int playerColor) {
        gp.startPve(playerColor);
        if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }
    
    public void back() {
    	client.disconnect();
    	gp.cleanReset();
    	if (chatPanel != null) chatPanel.clear();
    	cl.show(jp, "MENU");
    }
    
    public void updateStatus(String text, boolean restartFlag) {
    	if (status != null) {
    		status.setText(text);
    	}
    	if (rstBtn != null) {
    		rstBtn.setEnabled(restartFlag);
    	}
    }
    
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.dpiaware", "true");
        initScaleUI();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void initScaleUI() {
        try {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (dpi >= 144 || screenSize.width >= 2560) {
                scaleFactor = 1.35f;
            } else if (dpi >= 120 || screenSize.width >= 1920) {
                scaleFactor = 1.18f;
            } else {
                scaleFactor = 1.0f;
            }
            if (scaleFactor > 1.0f) {
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof FontUIResource) {
                        Font f = (Font) value;
                        Font newFont = new Font(f.getName(), f.getStyle(), Math.round(f.getSize() * scaleFactor));
                        UIManager.put(key, new FontUIResource(newFont));
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}