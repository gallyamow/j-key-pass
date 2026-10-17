package jkeypass.ui.util;

import javax.swing.*;
import java.awt.*;

public final class Dialogs {
	private Dialogs() {
	}

	public static void error(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
	}

	public static void warning(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Предупреждение", JOptionPane.WARNING_MESSAGE);
	}

	public static boolean confirm(Component parent, String message) {
		return JOptionPane.showConfirmDialog(parent, message, "Внимание",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
}
