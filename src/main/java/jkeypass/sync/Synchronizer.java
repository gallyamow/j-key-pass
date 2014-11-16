package jkeypass.sync;

import java.io.File;
import java.io.IOException;

public interface Synchronizer {
	public void save(File databaseFile) throws SyncException;

	public File load(File databaseFile) throws SyncException;
}
