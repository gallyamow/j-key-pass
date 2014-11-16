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

		this.setLocationByPlatform(true);
		
		this.panel = new AccountEditorPanel(account);
		this.add(this.panel);

		this.add(this.createButtons(), BorderLayout.SOUTH);

		this.pack();
	}

	public int showDialog() {
		this.setVisible(true);

		return this.result;
	}

	public Account getAccount() {
		return this.panel.getUpdatedAccount();
	}

	private JPanel createButtons() {
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

		this.getRootPane().setDefaultButton(saveButton);

		return buttonPanel;
	}
}
