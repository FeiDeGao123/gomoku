package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import gomoku.logic.GameCheck;

public class MainFrame extends JFrame {
    private CardLayout cl;
    private JPanel jp;
    private MenuPanel mp;
    private GridPanel gp;
    private GameCheck gc;
    
    public MainFrame() {
        this.setTitle("网络五子棋");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.gc = new GameCheck();
        cl = new CardLayout();
        jp = new JPanel(cl);
        mp = new MenuPanel(this);
        gp = new GridPanel(this, gc);
        jp.add(mp, "MENU");
        jp.add(gp, "GAME");
        this.add(jp);
        cl.show(jp, "MENU");
    }
    
    public void startLocalGame() {
    	gp.restart();
        cl.show(jp, "GAME");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}