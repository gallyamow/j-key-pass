package jkeypass.sync.dropbox;

import jkeypass.settings.StoredSettings;

import java.util.prefs.Preferences;

public final class DropboxSettings implements StoredSettings {
	private static final String KEY_TOKEN = "dropboxToken";

	private final Preferences preferences = Preferences.userRoot().node("j-key-pass").node("dropbox");

	private String token;

	public DropboxSettings() {
		token = preferences.get(KEY_TOKEN, "");
	}

	public String token() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public void save() {
		preferences.put(KEY_TOKEN, token);
	}
}
