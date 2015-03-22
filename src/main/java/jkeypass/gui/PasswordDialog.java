package jkeypass.gui;

import javax.swing.*;
import java.awt.*;

public class PasswordDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int SAVE_OPTION = 1;

	private int result = CANCEL_OPTION;

	private AccountEditorPanel panel;

	public PasswordDialog(Frame owner, String title) {
		super(owner, title, true);

		JPasswordField passwordField = new JPasswordField();
		
		JPanel panel = new JPanel();
		panel.add(passwordField);

		this.pack();
	}

	public int showDialog() {
		this.setVisible(true);

		return this.result;
	}
}
