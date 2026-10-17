package jkeypass.ui.dialog;

import jkeypass.domain.Account;
import jkeypass.ui.panel.AccountEditorPanel;

import javax.swing.*;
import java.awt.*;

public final class AccountDialog extends BaseDialog {
	private final AccountEditorPanel panel;

	public AccountDialog(Window owner, String title, Account account) {
		super(owner, title);

		panel = new AccountEditorPanel(account);
		add(panel);
		add(buttons("Сохранить"), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(owner);
	}

	public Account account() {
		return panel.account();
	}
}
