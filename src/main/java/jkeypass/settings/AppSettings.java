package jkeypass.settings;

import jkeypass.sync.SyncMethod;

import javax.swing.*;
import java.util.prefs.Preferences;

public final class AppSettings implements StoredSettings {
	private static final String NODE = "j-key-pass";
	private static final String KEY_THEME = "theme";
	private static final String KEY_SYNC_METHOD = "method";

	private final Preferences preferences = Preferences.userRoot().node(NODE);

	private String theme;
	private SyncMethod syncMethod;

	public AppSettings() {
		theme = preferences.get(KEY_THEME, UIManager.getSystemLookAndFeelClassName());
		syncMethod = SyncMethod.fromId(preferences.get(KEY_SYNC_METHOD, SyncMethod.NONE.id()));
	}

	public String theme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public SyncMethod syncMethod() {
		return syncMethod;
	}

	public void setSyncMethod(SyncMethod syncMethod) {
		this.syncMethod = syncMethod;
	}

	@Override
	public void save() {
		preferences.put(KEY_THEME, theme);
		preferences.put(KEY_SYNC_METHOD, syncMethod.id());
	}
}
