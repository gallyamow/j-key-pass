package jkeypass.gui;

import jkeypass.common.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int OK_OPTION = 1;

	private int result = CANCEL_OPTION;

	private JPasswordField passwordField;

	public PasswordDialog(Frame owner, String title) {
		super(owner, title, true);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setLayout(new GridBagLayout());

		passwordField = new JPasswordField();
		panel.add(passwordField, GridBagLayoutHelper.createGbc(0, 1));

		add(panel);
		add(createButtons(), BorderLayout.SOUTH);

		setLocationByPlatform(true);
		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	public int showDialog() {
		setVisible(true);

		return result;
	}

	public char[] getPassword() {
		return this.passwordField.getPassword();
	}

	private JPanel createButtons() {
		JPanel buttonPanel = new JPanel();

		JButton saveButton = new JButton("Ok");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = OK_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(saveButton);

		JButton closeButton = new JButton("Закрыть");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = CANCEL_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(closeButton);

		getRootPane().setDefaultButton(saveButton);

		return buttonPanel;
	}
}
