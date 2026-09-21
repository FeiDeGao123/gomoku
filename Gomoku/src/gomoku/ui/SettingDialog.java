package gomoku.ui;

import javax.swing.*;

import gomoku.util.SoundPlayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 游戏设置
 * 继承自 JDialog，对局设置
 */
public class SettingDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    public static final Color MODE1 = new Color(222, 184, 135);
    public static final Color MODE2 = new Color(238, 222, 192);
    public static final Color MODE3 = new Color(178, 136, 96);
    private StringBuilder inputBuffer = new StringBuilder();
    private JLabel displayLabel;

    /**
     * 构造方法，初始化窗口
     * @param mf 主窗口引用，用于更换 GridPanel 棋盘
     */
    public SettingDialog(MainFrame mf) {
        super(mf, "游戏个性化设置", true);
        this.setSize(380, 520);
        this.setLocationRelativeTo(mf);
        this.setResizable(false);
        this.setLayout(new BorderLayout(8, 8));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        mainPanel.setBackground(new Color(248, 245, 240));
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        themePanel.setOpaque(false);
        themePanel.setBorder(BorderFactory.createTitledBorder("棋盘背景主题"));
        JRadioButton theme1Btn = new JRadioButton("主题1", true);
        JRadioButton theme2Btn = new JRadioButton("主题2");
        JRadioButton theme3Btn = new JRadioButton("主题3");
        theme1Btn.setOpaque(false); 
        theme2Btn.setOpaque(false); 
        theme3Btn.setOpaque(false);
        ButtonGroup themeGroup = new ButtonGroup();
        themeGroup.add(theme1Btn);
        themeGroup.add(theme2Btn);
        themeGroup.add(theme3Btn);
        ActionListener themeListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (theme1Btn.isSelected()) mf.getGridPanel().setBoardColor(MODE1);
                else if (theme2Btn.isSelected()) mf.getGridPanel().setBoardColor(MODE2);
                else if (theme3Btn.isSelected()) mf.getGridPanel().setBoardColor(MODE3);
            }
        };
        theme1Btn.addActionListener(themeListener);
        theme2Btn.addActionListener(themeListener);
        theme3Btn.addActionListener(themeListener);
        themePanel.add(theme1Btn);
        themePanel.add(theme2Btn);
        themePanel.add(theme3Btn);
        mainPanel.add(themePanel);
        mainPanel.add(Box.createVerticalStrut(8));
        JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        soundPanel.setOpaque(false);
        soundPanel.setBorder(BorderFactory.createTitledBorder("声音音效设置"));
        JCheckBox cbSound = new JCheckBox("落子音效", SoundPlayer.isSoundEnabled());
        JCheckBox cbBgm = new JCheckBox("背景音乐", false);
        cbSound.setOpaque(false); 
        cbBgm.setOpaque(false);
        cbSound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundPlayer.setSoundEnabled(cbSound.isSelected());
            }
        });
        soundPanel.add(cbSound);
        soundPanel.add(cbBgm);
        mainPanel.add(soundPanel);
        mainPanel.add(Box.createVerticalStrut(8));
        JPanel authPanel = new JPanel(new BorderLayout(4, 4));
        displayLabel = new JLabel("000000", SwingConstants.CENTER);
        displayLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        displayLabel.setForeground(new Color(90, 52, 28));
        displayLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 140, 100)),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        authPanel.add(displayLabel, BorderLayout.NORTH);
        JPanel numPadPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        numPadPanel.setOpaque(false);
        numPadPanel.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton numBtn = new JButton(String.valueOf(num));
            numBtn.setFont(new Font("微软雅黑", Font.BOLD, 15));
            numBtn.setFocusPainted(false);
            numBtn.setBackground(new Color(230, 215, 195));
            numBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (inputBuffer.length() < 6) {
                        inputBuffer.append(num);
                        updateDisplay();
                        checkPassword();
                    }
                }
            });
            numPadPanel.add(numBtn);
        }
        authPanel.add(numPadPanel, BorderLayout.CENTER);
        JButton clearBtn = new JButton("清空输入");
        clearBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputBuffer.setLength(0);
                updateDisplay();
            }
        });
        authPanel.add(clearBtn, BorderLayout.SOUTH);
        mainPanel.add(authPanel);
        this.add(mainPanel, BorderLayout.CENTER);
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        JButton closeBtn = new JButton("完成");
        closeBtn.setFont(new Font("微软雅黑", Font.BOLD, 13));
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottomBar.add(closeBtn);
        this.add(bottomBar, BorderLayout.SOUTH);
    }

    /**
     * 刷新电子密码屏幕文本
     * 若缓冲区为空显示默认占位符 "000000"，否则显示当前输入的数字串
     */
    private void updateDisplay() {
        if (inputBuffer.length() == 0) {
            displayLabel.setText("000000");
        } else {
            displayLabel.setText(inputBuffer.toString());
        }
    }

    /**
     * 匹配特定字符串 "114514"，若输满 6 位非匹配密码则自动重置
     */
    private void checkPassword() {
        String code = inputBuffer.toString();
        if (code.equals("114514")) {
            JOptionPane.showMessageDialog(this, 
                "man!", 
                "成功", 
                JOptionPane.INFORMATION_MESSAGE);
            inputBuffer.setLength(0);
            updateDisplay();
        } else if (code.length() >= 6) {
            inputBuffer.setLength(0);
            updateDisplay();
        }
    }
}