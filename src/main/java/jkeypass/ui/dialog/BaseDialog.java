package jkeypass.ui.dialog;

import javax.swing.*;
import java.awt.*;

public abstract class BaseDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int OK_OPTION = 1;

	private int result = CANCEL_OPTION;

	protected BaseDialog(Window owner, String title) {
		super(owner, title, ModalityType.APPLICATION_MODAL);
	}

	public int showDialog() {
		setVisible(true);
		return result;
	}

	protected void accept() {
		result = OK_OPTION;
		dispose();
	}

	protected void cancel() {
		result = CANCEL_OPTION;
		dispose();
	}

	protected JPanel buttons(String okLabel) {
		JButton okButton = new JButton(okLabel);
		okButton.addActionListener(e -> accept());

		JButton closeButton = new JButton("Закрыть");
		closeButton.addActionListener(e -> cancel());

		JPanel panel = new JPanel();
		panel.add(okButton);
		panel.add(closeButton);

		getRootPane().setDefaultButton(okButton);

		return panel;
	}
}
