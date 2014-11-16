package jkeypass.gui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
	private Frame frame;
	private JLabel label;

	public StatusBar(Frame frame) {
		this.frame = frame;

		//this.setBorder(new BevelBorder(BevelBorder.RAISED));
		this.setPreferredSize(new Dimension(frame.getWidth(), 20));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.label = new JLabel();
		this.label.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(label);
	}

	public void setText(String text) {
		this.label.setText(text);
	}

	public void clear() {
		this.setText("");
	}
}
