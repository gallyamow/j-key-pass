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
		this.preferences = Preferences.userRoot().node("j-key-pass");

		this.initParams();
	}

	public void save() {
		this.preferences.put(Options.THEME.getKey(), this.getTheme());
		this.preferences.put(Options.SYNC_METHOD.getKey(), this.getSyncMethod());
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return this.theme;
	}

	public void setSyncMethod(String syncMethod) {
		this.syncMethod = syncMethod;
	}

	public String getSyncMethod() {
		return syncMethod;
	}

	private void initParams() {
		this.setTheme(this.preferences.get(Options.THEME.getKey(), UIManager.getSystemLookAndFeelClassName()));
		this.setSyncMethod(this.preferences.get(Options.SYNC_METHOD.getKey(), Sync.Method.NOSYNC.getName()));
	}
}
