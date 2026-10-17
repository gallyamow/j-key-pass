package jkeypass.ui.panel;

import jkeypass.settings.StoredSettings;
import jkeypass.sync.dropbox.DropboxSettings;
import jkeypass.ui.util.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;

public final class DropboxSettingsForm extends SyncSettingsForm {
	private final JTextArea tokenArea;

	public DropboxSettingsForm(DropboxSettings settings) {
		setLayout(new GridBagLayout());

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(new JLabel("Access token:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, 0));

		tokenArea = new JTextArea(settings.token(), 3, 30);
		add(new JScrollPane(tokenArea), GridBagLayoutHelper.gbc(1, 0));
	}

	@Override
	public void applyTo(StoredSettings settings) {
		((DropboxSettings) settings).setToken(tokenArea.getText().trim());
	}
}
