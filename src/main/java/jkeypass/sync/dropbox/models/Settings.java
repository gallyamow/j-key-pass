package jkeypass.sync.dropbox.models;

import java.util.prefs.Preferences;

public class Settings implements jkeypass.common.Settings {
	public enum Options {
		KEY("App key", "dropboxKey"),
		SECRET("App secret", "dropboxSecret"),
		TOKEN("Access token", "dropboxToken");

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

	private String key;
	private String secret;
	private String token;

	private Preferences preferences;

	public Settings() {
		preferences = Preferences.userRoot().node("j-key-pass").node("dropbox");

		initParams();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void save() {
		preferences.put(Options.KEY.getKey(), getKey());
		preferences.put(Options.SECRET.getKey(), getSecret());
		preferences.put(Options.TOKEN.getKey(), getToken());
	}

	private void initParams() {
		setKey(preferences.get(Options.KEY.getKey(), ""));
		setSecret(preferences.get(Options.SECRET.getKey(), ""));
		setToken(preferences.get(Options.TOKEN.getKey(), ""));
	}
}
