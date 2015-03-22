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

		panel = new SettingsPanel(settings);
		add(panel);

		add(createButtons(), BorderLayout.SOUTH);

		setResizable(false);
		setLocationByPlatform(true);
		pack();
	}

	public int showDialog() {
		setVisible(true);

		return result;
	}

	private void save() {
		settings = panel.getUpdatedSettings();
		settings.save();
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

		getRootPane().setDefaultButton(saveButton);

		return buttonPanel;
	}
}
