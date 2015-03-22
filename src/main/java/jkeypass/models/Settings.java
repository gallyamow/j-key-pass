package jkeypass.models;

import jkeypass.sync.Sync;

import javax.swing.*;
import java.util.prefs.Preferences;

public class Settings implements jkeypass.common.Settings {
	public enum Options {
		THEME("Тема", "theme"),
		SYNC_METHOD("Синхронизация", "method");

		private String label;
		private String key;

		private Options(String label, String key) {
			this.label = label;
			this.key = key;
		}

		public String getLabel() {
			return label;
		}

		public String getKey() {
			return key;
		}
	}

	private String theme;
	private String syncMethod;

	private Preferences preferences;

	public Settings() {
		preferences = Preferences.userRoot().node("j-key-pass");

		initParams();
	}

	public void save() {
		preferences.put(Options.THEME.getKey(), getTheme());
		preferences.put(Options.SYNC_METHOD.getKey(), getSyncMethod());
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return theme;
	}

	public void setSyncMethod(String syncMethod) {
		this.syncMethod = syncMethod;
	}

	public String getSyncMethod() {
		return syncMethod;
	}

	private void initParams() {
		setTheme(preferences.get(Options.THEME.getKey(), UIManager.getSystemLookAndFeelClassName()));
		setSyncMethod(preferences.get(Options.SYNC_METHOD.getKey(), Sync.Method.NOSYNC.getName()));
	}
}
