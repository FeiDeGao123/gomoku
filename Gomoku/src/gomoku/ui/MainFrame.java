package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import gomoku.logic.GameCheck;
import gomoku.network.GameClient;
import gomoku.network.SocketClient;

public class MainFrame extends JFrame {
    private CardLayout cl;
    private JPanel jp;
    private MenuPanel mp;
    private GridPanel gp;
    private GameCheck gc;
    private GameClient client;
    
    public MainFrame() {
        this.setTitle("网络五子棋");
        this.setSize(800, 800);
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
        jp.add(mp, "MENU");
        jp.add(gp, "GAME");
        this.add(jp);
        cl.show(jp, "MENU");
    }
    
    public void startLocalGame() {
    	gp.restart();
        cl.show(jp, "GAME");
    }

    public void startOnlineGame() {
        cl.show(jp, "GAME");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}