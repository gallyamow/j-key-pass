package jkeypass.ui.dialog;

import jkeypass.settings.StoredSettings;
import jkeypass.sync.SyncMethod;
import jkeypass.sync.SynchronizerFactory;
import jkeypass.ui.panel.SyncSettingsForm;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public final class SyncSettingsDialog extends BaseDialog {
	private final StoredSettings settings;
	private final SyncSettingsForm form;

	public SyncSettingsDialog(Window owner, SyncMethod method) {
		super(owner, "Настройки синхронизации");

		Optional<StoredSettings> found = SynchronizerFactory.settingsFor(method);
		if (found.isEmpty()) {
			throw new IllegalArgumentException("Метод " + method + " не имеет настроек");
		}
		settings = found.get();

		form = SyncSettingsForm.create(method, settings);
		add(form);
		add(buttons("Сохранить"), BorderLayout.SOUTH);

		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	@Override
	protected void accept() {
		form.applyTo(settings);
		settings.save();
		super.accept();
	}
}
