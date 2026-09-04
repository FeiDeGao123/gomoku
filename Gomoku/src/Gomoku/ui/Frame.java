package Gomoku.ui;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GridPanel gridPanel;
    
    public Frame() {
        this.setTitle("网络五子棋");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        menuPanel = new MenuPanel(this);
        gridPanel = new GridPanel(this);
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gridPanel, "GAME");
        this.add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }
    
    public void startLocalGame() {
        gridPanel.restart();
        cardLayout.show(mainPanel, "GAME");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Frame frame = new Frame();
            frame.setVisible(true);
        });
    }
}