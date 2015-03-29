package jkeypass.gui;

import jkeypass.models.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccountDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int SAVE_OPTION = 1;

	private int result = CANCEL_OPTION;

	private AccountEditorPanel panel;

	public AccountDialog(Account account, Frame owner, String title) {
		super(owner, title, true);

		panel = new AccountEditorPanel(account);
		add(panel);

		add(buttons(), BorderLayout.SOUTH);

		setLocationByPlatform(true);
		pack();
	}

	public int showDialog() {
		setVisible(true);

		return result;
	}

	public Account getAccount() {
		return panel.getUpdatedAccount();
	}

	private JPanel buttons() {
		JPanel buttonPanel = new JPanel();

		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = SAVE_OPTION;
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
