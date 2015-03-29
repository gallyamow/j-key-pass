package jkeypass.gui;

import jkeypass.common.Settings;
import jkeypass.common.SettingsPanel;
import jkeypass.sync.Sync;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SyncSettingsDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int SAVE_OPTION = 1;

	private int result = CANCEL_OPTION;

	private Settings settings;
	private SettingsPanel panel;

	public SyncSettingsDialog(Dialog owner, String methodName) {
		super(owner, "Настройки синхронизации", true);

		settings = Sync.getSettings(methodName);

		panel = Sync.getSettingsPanel(methodName, settings);

		add((JComponent) panel);

		add(buttons(), BorderLayout.SOUTH);

		pack();
		setResizable(false);
	}

	public int showDialog() {
		setVisible(true);

		return result;
	}

	private void save() {
		settings = panel.getUpdatedSettings();

		settings.save();
	}

	private JPanel buttons() {
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
