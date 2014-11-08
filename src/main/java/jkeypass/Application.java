package jkeypass;

import jkeypass.gui.MainFrame;

import javax.swing.*;
import java.io.File;

public class Application {
	private File databaseFile;

	public static void main(String[] args) {
		Application application = new Application();
		application.run();
	}

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame = new MainFrame();
				mainFrame.setVisible(true);
			}
		});
	}
}
