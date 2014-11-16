package jkeypass.gui;

import jkeypass.models.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int SAVE_OPTION = 1;

	private int result = CANCEL_OPTION;

	private Settings settings;

	private SettingsPanel panel;

	public SettingsDialog(Frame owner, String title, Settings settings) {
		super(owner, title, true);

		this.settings = settings;

		this.panel = new SettingsPanel(this.settings);
		this.add(this.panel);

		this.add(this.createButtons(), BorderLayout.SOUTH);

		this.pack();
		this.setResizable(false);
	}

	public int showDialog() {
		this.setVisible(true);

		return this.result;
	}

	private void save() {
		this.settings = this.panel.getUpdatedSettings();
		this.settings.save();
	}

	private JPanel createButtons() {
		JPanel buttonPanel = new JPanel();

		JButton saveButton = new JButton("Сохранить");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
				result = SAVE_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(saveButton);

		JButton closeButton = new JButton("Закрыть");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = CANCEL_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(closeButton);

		this.getRootPane().setDefaultButton(saveButton);

		return buttonPanel;
	}
}
