package jkeypass.ui;

import javax.swing.*;
import java.awt.*;

public final class StatusBar extends JPanel {
	private final JLabel label = new JLabel();

	public StatusBar() {
		setPreferredSize(new Dimension(0, 22));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(label);
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void clear() {
		setText("");
	}
}
