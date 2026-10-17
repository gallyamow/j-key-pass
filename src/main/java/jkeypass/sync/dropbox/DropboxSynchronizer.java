package jkeypass.sync.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import jkeypass.sync.SyncException;
import jkeypass.sync.Synchronizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class DropboxSynchronizer implements Synchronizer {
	private final DbxClientV2 client;

	public DropboxSynchronizer(String accessToken) {
		DbxRequestConfig config = DbxRequestConfig.newBuilder("j-key-pass").build();
		client = new DbxClientV2(config, accessToken);
	}

	@Override
	public void save(Path localFile) throws SyncException {
		try (InputStream in = Files.newInputStream(localFile)) {
			client.files()
					.uploadBuilder(remotePath(localFile))
					.withMode(WriteMode.OVERWRITE)
					.uploadAndFinish(in);
		} catch (Exception e) {
			throw new SyncException("Не удалось сохранить файл в удалённое хранилище", e);
		}
	}

	@Override
	public Optional<Path> load(Path localFile) throws SyncException {
		Path tempFile = localFile.resolveSibling(localFile.getFileName() + ".dropbox");

		try (OutputStream out = Files.newOutputStream(tempFile)) {
			client.files()
					.downloadBuilder(remotePath(localFile))
					.download(out);
			return Optional.of(tempFile);
		} catch (DownloadErrorException e) {
			deleteQuietly(tempFile);
			if (e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound()) {
				return Optional.empty();
			}
			throw new SyncException("Не удалось загрузить файл из удалённого хранилища", e);
		} catch (Exception e) {
			deleteQuietly(tempFile);
			throw new SyncException("Не удалось загрузить файл из удалённого хранилища", e);
		}
	}

	private String remotePath(Path localFile) {
		return "/" + localFile.getFileName();
	}

	private void deleteQuietly(Path file) {
		try {
			Files.deleteIfExists(file);
		} catch (IOException ignored) {
		}
	}
}
