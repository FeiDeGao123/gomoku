package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.dao.User;
import gomoku.dao.DataProcessing;
import gomoku.dao.IDataProcessing;

/**
 * 用户登录与注册
 * 继承自 JDialog，负责收集用户输入的账号与密码
 */
public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
    private JTextField userField;
    private JPasswordField passField;
    private IDataProcessing dp = new DataProcessing();
    private User loggedInUser = null;

    /**
     * 无参构造方法
     */
    public LoginDialog() {
        this(null);
    }

    /**
     * 构造方法，初始化登录窗口界面组件与 GridBagLayout 布局
     * @param parent 依赖的父级窗口
     */
    public LoginDialog(Frame parent) {
        super(parent, "用户登录 / 注册", true);
        this.setSize(340, 220);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 235, 225));
        JLabel userLabel = new JLabel("账号:");
        GridBagConstraints gbcUserLabel = new GridBagConstraints();
        gbcUserLabel.insets = new Insets(8, 8, 8, 8);
        gbcUserLabel.gridx = 0;
        gbcUserLabel.gridy = 0;
        panel.add(userLabel, gbcUserLabel);
        userField = new JTextField(12);
        GridBagConstraints gbcUserField = new GridBagConstraints();
        gbcUserField.insets = new Insets(8, 8, 8, 8);
        gbcUserField.gridx = 1;
        gbcUserField.gridy = 0;
        panel.add(userField, gbcUserField);
        JLabel passLabel = new JLabel("密码:");
        GridBagConstraints gbcPassLabel = new GridBagConstraints();
        gbcPassLabel.insets = new Insets(8, 8, 8, 8);
        gbcPassLabel.gridx = 0;
        gbcPassLabel.gridy = 1;
        panel.add(passLabel, gbcPassLabel);
        passField = new JPasswordField(12);
        GridBagConstraints gbcPassField = new GridBagConstraints();
        gbcPassField.insets = new Insets(8, 8, 8, 8);
        gbcPassField.gridx = 1;
        gbcPassField.gridy = 1;
        panel.add(passField, gbcPassField);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        JButton loginBtn = new JButton("登录");
        JButton regBtn = new JButton("注册");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (u.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "请输入账号和密码！");
                    return;
                }
                User resUser = dp.login(u, p);
                if (resUser != null) {
                    loggedInUser = resUser;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "账号或密码错误！");
                }
            }
        });

        regBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (u.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "请输入要注册的账号和密码！");
                    return;
                }
                if (dp.register(u, p)) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "注册成功，请点击登录！");
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "该账号已存在，请换一个！");
                }
            }
        });
        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);
        GridBagConstraints gbcBtnPanel = new GridBagConstraints();
        gbcBtnPanel.insets = new Insets(8, 8, 8, 8);
        gbcBtnPanel.gridx = 0;
        gbcBtnPanel.gridy = 2;
        gbcBtnPanel.gridwidth = 2;
        panel.add(btnPanel, gbcBtnPanel);
        this.add(panel);
    }
    
    /**
     * 阻塞式调起登录对话框并获取登录结果实体
     * @return 登录成功返回当前玩家的 User 对象，若取消或关闭则返回 null
     */
    public User showDialog() {
        this.setVisible(true);
        return loggedInUser;
    }
}