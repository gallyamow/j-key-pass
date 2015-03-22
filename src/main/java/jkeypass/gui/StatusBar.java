package jkeypass.gui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
	private Frame frame;
	private JLabel label;

	public StatusBar(Frame frame) {
		this.frame = frame;

		setPreferredSize(new Dimension(frame.getWidth(), 20));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.LEFT);
		add(label);
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void clear() {
		setText("");
	}
}
