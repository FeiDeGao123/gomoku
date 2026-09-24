package gomoku.ui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;
import gomoku.dao.User;
import gomoku.dao.DataProcessing;
import gomoku.dao.IDataProcessing;
import gomoku.logic.GameCheck;
import gomoku.logic.IGameCheck;
import gomoku.network.IClient;
import gomoku.network.SocketClient;

/**
 * 五子棋控制主窗口
 * 继承自 JFrame，程序启动入口
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
    private static float scaleFactor = 1.0f;
    private CardLayout cl;
    private JPanel jp;
    private MenuPanel mp;
    private GridPanel gp;
    private ChatPanel chatPanel;
    private IGameCheck gc;
    private IDataProcessing dp = new DataProcessing();
    private IClient client;
    private JButton backBtn;
    private JButton rstBtn;
    private JButton undoBtn;
    private JButton giveUpBtn;
    private JButton settingBtn;
    private JLabel status;
    private JPanel bar;
    private User currUser;
    
    /**
     * 构造方法，初始化窗口、注入分层对象、组装布局、注册监听
     */
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
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                adjustTopBarUI();
            }
        });
    }

    /**
     * 获取当前登录玩家对象
     * @return 当前登录的 User 实体
     */
    public User getCurrUser() {
    	return currUser;
    }
    
    /**
     * 获取数据访问对象引用
     * @return DataProcessing 实例
     */
    public IDataProcessing getDataProcessing() {
    	return dp;
    }

    /**
     * 获取棋盘面板引用
     * @return GridPanel 实例
     */
    public GridPanel getGridPanel() {
        return gp;
    }

    /**
     * 获取聊天面板引用
     * @return ChatPanel 实例
     */
    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    /**
     * 设置当前登录用户，刷新主菜单大厅的用户信息展示栏
     * @param u 登录成功的 User 对象
     */
    public void setCurrUser(User u) {
        this.currUser = u;
        if (mp != null) {
            mp.updateUserInfo(currUser);
        }
    }

    /**
     * 提交对局结算
     * @param isWin    本局是否获胜
     * @param hexSteps 十六进制棋谱坐标串
     */
    public void settleMatch(boolean isWin, String hexSteps) {
        if (currUser != null) {
            dp.settleMatch(currUser.getID(), isWin, hexSteps);
            this.currUser = dp.getUserById(currUser.getID());
            if (mp != null) {
                mp.updateUserInfo(currUser);
            }
        }
    }

    public void settleMatch(int blackId, int whiteId, int winnerId, String hexSteps) {
        dp.settleMatch(blackId, whiteId, winnerId, hexSteps);
        if (currUser != null) {
            this.currUser = dp.getUserById(currUser.getID());
            if (mp != null) {
                mp.updateUserInfo(currUser);
            }
        }
    }
    
    /**
     * 创建对局界面
     * BorderLayout，北部放控制工具条 bar，中间放棋盘 gp，东侧放聊天框 chatPanel
     * @return 完整的游戏室卡片面板
     */
    private JPanel createBar() {
    	JPanel option = new JPanel(new BorderLayout());
    	bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
    	bar.setBackground(new Color(205, 168, 120));
    	backBtn = new JButton("返回");
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
        settingBtn = new JButton("设置");
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

    /**
     * 动态调整顶部工具条排版与字号大小，防止全屏或大窗口下工具条按钮字号过小
     */
    private void adjustTopBarUI() {
        int w = getWidth();
        if (w <= 0 || bar == null) return;
        float dynamicScale = Math.max(1.0f, Math.min(1.8f, w / 920.0f));
        Font btnFont = new Font("微软雅黑", Font.BOLD, Math.round(14 * dynamicScale));
        if (backBtn != null) backBtn.setFont(btnFont);
        if (rstBtn != null) rstBtn.setFont(btnFont);
        if (undoBtn != null) undoBtn.setFont(btnFont);
        if (giveUpBtn != null) giveUpBtn.setFont(btnFont);
        if (settingBtn != null) settingBtn.setFont(btnFont);
        if (status != null) status.setFont(new Font("微软雅黑", Font.BOLD, Math.round(15 * dynamicScale)));
        if (chatPanel != null) {
            chatPanel.setPreferredSize(new Dimension(Math.round(200 * dynamicScale), 0));
        }
        bar.revalidate();
        bar.repaint();
    }
    
    /**
     * 启动单机双人对战，切换卡片至游戏区
     */
    public void startLocalGame() {
    	gp.restart();
    	if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }

    /**
     * 联机匹配成功后调起，切换卡片至游戏区
     */
    public void startOnlineGame() {
    	if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }
    
    /**
     * 启动人机对战模式
     * @param playerColor 玩家执棋颜色
     * @param level AI 难度等级 
     */
    public void startPveGame(int playerColor, int level) {
        gp.startPve(playerColor, level);
        if (chatPanel != null) chatPanel.clear();
        cl.show(jp, "GAME");
    }

    public void startPveGame(int playerColor) {
        startPveGame(playerColor, gomoku.logic.GameAI.EASY);
    }
    
    /**
     * 从游戏区返回主菜单大厅
     * 断开 Socket 连接、清空棋盘与聊天记录，并切换回 MENU 卡片
     */
    public void back() {
    	client.disconnect();
    	gp.cleanReset();
    	if (chatPanel != null) chatPanel.clear();
    	cl.show(jp, "MENU");
    }
    
    /**
     * 刷新顶部状态提示文字并控制重新开始按钮的可用状态
     * @param text 提示文本
     * @param restartFlag 重新开始按钮是否使能可用
     */
    public void updateStatus(String text, boolean restartFlag) {
    	if (status != null) {
    		status.setText(text);
    	}
    	if (rstBtn != null) {
    		rstBtn.setEnabled(restartFlag);
    	}
    }
    
    /**
     * 主入口
     * @param args 命令行启动参数
     */
    public static void main(String[] args) {
    	System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.dpiaware", "true");
        initScaleUI();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * 通过 Toolkit 探测屏幕物理分辨率与 DPI，得到缩放倍率，
     * 并遍历 UIManager 配置项，将所有 Swing 控件字体按比例放大
     */
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
        } catch (Exception e) {}
    }
}