package jkeypass.sync.dropbox.models;

import java.util.prefs.Preferences;

public class DropboxSettings implements jkeypass.common.Settings {
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

	public DropboxSettings() {
		this.preferences = Preferences.userRoot().node("j-key-pass").node("dropbox");

		this.initParams();
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
		this.preferences.put(Options.KEY.getKey(), this.getKey());
		this.preferences.put(Options.SECRET.getKey(), this.getSecret());
		this.preferences.put(Options.TOKEN.getKey(), this.getToken());
	}

	private void initParams() {
		this.setKey(this.preferences.get(Options.KEY.getKey(), ""));
		this.setSecret(this.preferences.get(Options.SECRET.getKey(), ""));
		this.setToken(this.preferences.get(Options.TOKEN.getKey(), ""));
	}
}
