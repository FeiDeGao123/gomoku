package gomoku.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gomoku.dao.User;
import gomoku.dao.DataProcessing;

public class LoginDialog extends JDialog {
	private static final long serialVersionUID = 1L;
    private JTextField userField;
    private JPasswordField passField;
    private DataProcessing dp = new DataProcessing();
    private User loggedInUser = null;

    public LoginDialog() {
        this(null);
    }

    public LoginDialog(Frame parent) {
        super(parent, "”√ªßµ«¬º / ◊¢≤·", true);
        this.setSize(340, 220);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 235, 225));
        JLabel userLabel = new JLabel("’À∫≈:");
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
        JLabel passLabel = new JLabel("√‹¬Î:");
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
        JButton loginBtn = new JButton("µ«¬º");
        JButton regBtn = new JButton("◊¢≤·");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (u.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "«Î ‰»Î’À∫≈∫Õ√‹¬Î£°");
                    return;
                }
                User resUser = dp.login(u, p);
                if (resUser != null) {
                    loggedInUser = resUser;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "’À∫≈ªÚ√‹¬Î¥ÌŒÛ£°");
                }
            }
        });

        regBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword()).trim();
                if (u.isEmpty() || p.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "«Î ‰»Î“™◊¢≤·µƒ’À∫≈∫Õ√‹¬Î£°");
                    return;
                }
                if (dp.register(u, p)) {
                    JOptionPane.showMessageDialog(LoginDialog.this, "◊¢≤·≥…π¶£¨«Îµ„ª˜µ«¬º£°");
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, "∏√’À∫≈“—¥Ê‘⁄£¨«Îªª“ª∏ˆ£°");
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

    public User showDialog() {
        this.setVisible(true);
        return loggedInUser;
    }
}