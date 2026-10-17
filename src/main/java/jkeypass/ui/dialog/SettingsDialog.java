package jkeypass.ui.dialog;

import jkeypass.settings.AppSettings;
import jkeypass.ui.panel.GeneralSettingsPanel;

import javax.swing.*;
import java.awt.*;

public final class SettingsDialog extends BaseDialog {
	private final AppSettings settings;
	private final GeneralSettingsPanel panel;

	public SettingsDialog(Window owner, AppSettings settings) {
		super(owner, "Настройки");

		this.settings = settings;

		panel = new GeneralSettingsPanel(settings);
		add(panel);
		add(buttons("Сохранить"), BorderLayout.SOUTH);

		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	@Override
	protected void accept() {
		panel.applyTo(settings);
		settings.save();
		super.accept();
	}
}
