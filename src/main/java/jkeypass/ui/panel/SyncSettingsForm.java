package jkeypass.ui.panel;

import jkeypass.settings.StoredSettings;
import jkeypass.sync.SyncMethod;
import jkeypass.sync.dropbox.DropboxSettings;

import javax.swing.*;

public abstract class SyncSettingsForm extends JPanel {
	public abstract void applyTo(StoredSettings settings);

	public static SyncSettingsForm create(SyncMethod method, StoredSettings settings) {
		return switch (method) {
			case DROPBOX -> new DropboxSettingsForm((DropboxSettings) settings);
			case NONE -> throw new IllegalArgumentException("No settings for " + method);
		};
	}
}
