package jkeypass.ui.dialog;

import jkeypass.ui.util.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public final class PasswordDialog extends BaseDialog {
	private final JPasswordField passwordField = new JPasswordField(20);
	private final JPasswordField confirmField;

	private PasswordDialog(Window owner, boolean confirm) {
		super(owner, "Введите главный пароль");

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int row = 0;
		panel.add(new JLabel("Пароль:"), GridBagLayoutHelper.gbc(0, row));
		panel.add(passwordField, GridBagLayoutHelper.gbc(1, row));

		if (confirm) {
			row++;
			confirmField = new JPasswordField(20);
			panel.add(new JLabel("Повторите пароль:"), GridBagLayoutHelper.gbc(0, row));
			panel.add(confirmField, GridBagLayoutHelper.gbc(1, row));
		} else {
			confirmField = null;
		}

		add(panel);
		add(buttons("Ok"), BorderLayout.SOUTH);

		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	/**
	 * Dialog for entering the password of an existing database.
	 */
	public static PasswordDialog forOpen(Window owner) {
		return new PasswordDialog(owner, false);
	}

	/**
	 * Dialog for choosing the password of a new database (with confirmation).
	 */
	public static PasswordDialog forCreate(Window owner) {
		return new PasswordDialog(owner, true);
	}

	public char[] password() {
		return passwordField.getPassword();
	}

	public boolean passwordsMatch() {
		return confirmField == null || Arrays.equals(passwordField.getPassword(), confirmField.getPassword());
	}
}
