package jkeypass.sync;

import jkeypass.common.Settings;
import jkeypass.common.SettingsPanel;
import jkeypass.sync.dropbox.gui.DropboxSettingsPanel;
import jkeypass.sync.dropbox.models.DropboxSettings;

public class Sync {
	public enum Method {
		NOSYNC("nosync", "Без синхронизации"),
		DROPBOX("dropbox", "Dropbox");

		private String name;
		private String label;

		Method(String name, String label) {
			this.name = name;
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return this.getLabel();
		}
	}

	public static Settings getSettings(Method method) {
		switch (method) {
			case DROPBOX:
				return new DropboxSettings();
		}

		return null;
	}

	public static SettingsPanel getSettingsPanel(Method method, Settings settings) {
		switch (method) {
			case DROPBOX:
				return new DropboxSettingsPanel(settings);
		}

		return null;
	}
}
