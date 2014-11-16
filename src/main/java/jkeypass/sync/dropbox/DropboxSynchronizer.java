package jkeypass.sync.dropbox;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;
import jkeypass.sync.SyncException;
import jkeypass.sync.Synchronizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class DropboxSynchronizer implements Synchronizer {
	private DbxClient client;

	public DropboxSynchronizer(String key, String secret, String token) {
		DbxRequestConfig config = new DbxRequestConfig("j-key-pass", Locale.getDefault().toString());

		this.client = new DbxClient(config, token);
	}

	public void save(File databaseFile) throws SyncException {
		try {
			try (FileInputStream inputStream = new FileInputStream(databaseFile)) {
				try {
					this.client.uploadFile(this.getFilePathOnDropbox(databaseFile), DbxWriteMode.force(), databaseFile.length(), inputStream);
				} catch (Exception e) {
					throw new SyncException("Не удалось сохранить файл в удаленное хранилище", e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File load(File databaseFile) throws SyncException {
		File dropboxFile = new File(databaseFile.getParent(), databaseFile.getName() + ".dropbox");

//		File dropboxFile = File.createTempFile("dropbox", "dropbox");

		try {
			try (FileOutputStream outputStream = new FileOutputStream(dropboxFile)) {
				try {
					this.client.getFile(this.getFilePathOnDropbox(databaseFile), null, outputStream);
				} catch (Exception e) {
					throw new SyncException("Не удалось загрузить файл из удаленного хранилища", e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dropboxFile;
	}

	private String getFilePathOnDropbox(File file) {
		return "/" + file.getName();
	}
}
