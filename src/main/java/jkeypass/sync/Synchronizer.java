package jkeypass.sync;

import java.nio.file.Path;
import java.util.Optional;

public interface Synchronizer {
	void save(Path localFile) throws SyncException;

	/**
	 * Downloads the remote copy into a temporary file next to the local one.
	 * Returns empty if there is no remote copy yet.
	 */
	Optional<Path> load(Path localFile) throws SyncException;
}
