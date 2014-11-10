package jkeypass.models;

import javax.swing.*;
import java.util.prefs.Preferences;

public class Settings {
	public enum Options {
		THEME("Тема", "theme");

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

	private Preferences preferences;

	public Settings() {
		this.preferences = Preferences.userRoot().node("j-key-pass");

		this.initParams();
	}

	public void save() {
		this.preferences.put(Options.THEME.getKey(), this.theme);
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return this.theme;
	}

	private void initParams() {
		this.theme = this.preferences.get(Options.THEME.getKey(), UIManager.getSystemLookAndFeelClassName());
	}
}
