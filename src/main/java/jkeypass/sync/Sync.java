package jkeypass.sync;

import jkeypass.sync.dropbox.DropboxSynchronizer;
import jkeypass.sync.dropbox.gui.SettingsPanel;
import jkeypass.sync.dropbox.models.Settings;

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

		public static Method getByName(String name) {
			for (Method method : values()) {
				if (method.getName().equals(name)) {
					return method;
				}
			}

			return null;
		}

		@Override
		public String toString() {
			return getLabel();
		}
	}

	public static Synchronizer getSynchronizer(String methodName) {
		Method method = Method.getByName(methodName);

		switch (method) {
			case DROPBOX:
				Settings settings = new Settings();
				return new DropboxSynchronizer(settings.getKey(), settings.getSecret(), settings.getToken());
		}

		return null;
	}

	public static jkeypass.common.Settings getSettings(String methodName) {
		Method method = Method.getByName(methodName);

		switch (method) {
			case DROPBOX:
				return new Settings();
		}

		return null;
	}

	public static jkeypass.common.SettingsPanel getSettingsPanel(String methodName, jkeypass.common.Settings settings) {
		Method method = Method.getByName(methodName);

		switch (method) {
			case DROPBOX:
				return new SettingsPanel(settings);
		}

		return null;
	}
}
