package jkeypass;

import jkeypass.ui.MainFrame;

import javax.swing.*;

public final class Application {
	public static final String APP_NAME = "j-key-pass";
	public static final String DATABASE_EXTENSION = "jkp";

	private Application() {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
	}
}
