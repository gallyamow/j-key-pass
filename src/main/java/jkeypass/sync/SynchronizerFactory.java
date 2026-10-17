package jkeypass.sync;

import jkeypass.settings.StoredSettings;
import jkeypass.sync.dropbox.DropboxSettings;
import jkeypass.sync.dropbox.DropboxSynchronizer;

import java.util.Optional;

public final class SynchronizerFactory {
	private SynchronizerFactory() {
	}

	public static Optional<Synchronizer> create(SyncMethod method) {
		return switch (method) {
			case NONE -> Optional.empty();
			case DROPBOX -> {
				DropboxSettings settings = new DropboxSettings();
				yield settings.token().isBlank()
						? Optional.<Synchronizer>empty()
						: Optional.of(new DropboxSynchronizer(settings.token()));
			}
		};
	}

	public static Optional<StoredSettings> settingsFor(SyncMethod method) {
		return switch (method) {
			case NONE -> Optional.empty();
			case DROPBOX -> Optional.of(new DropboxSettings());
		};
	}
}
