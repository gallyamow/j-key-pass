package jkeypass.ui;

import jkeypass.Application;
import jkeypass.domain.Account;
import jkeypass.importer.ImportException;
import jkeypass.repository.CorruptDatabaseException;
import jkeypass.repository.WrongPasswordException;
import jkeypass.service.DatabaseService;
import jkeypass.service.ImportService;
import jkeypass.settings.AppSettings;
import jkeypass.sync.SyncException;
import jkeypass.sync.Synchronizer;
import jkeypass.sync.SynchronizerFactory;
import jkeypass.ui.dialog.AccountDialog;
import jkeypass.ui.dialog.BaseDialog;
import jkeypass.ui.dialog.ImportDialog;
import jkeypass.ui.dialog.PasswordDialog;
import jkeypass.ui.dialog.SettingsDialog;
import jkeypass.ui.util.Dialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Coordinates the UI and the services: all user flows (open/create/save
 * database, account CRUD, import, settings, sync) live here, keeping the
 * frame and dialogs free of business logic. Blocking IO runs in
 * {@link SwingWorker}s so the UI stays responsive.
 */
public final class AppController {
	private final MainFrame frame;
	private final DatabaseService database = new DatabaseService();
	private final ImportService importService = new ImportService();
	private final AppSettings settings = new AppSettings();

	private Synchronizer synchronizer;

	public AppController(MainFrame frame) {
		this.frame = frame;
	}

	public void init() {
		applyLookAndFeel(false);
		synchronizer = SynchronizerFactory.create(settings.syncMethod()).orElse(null);
	}

	public DatabaseService database() {
		return database;
	}

	public void createDatabase() {
		Path file = chooseFile(true);
		if (file == null || !confirmLock(file) || !closeCurrentDatabase()) {
			return;
		}

		char[] password = requestPassword(true);
		if (password == null) {
			return;
		}

		try {
			database.create(file, password);
			frame.onDatabaseChanged();
		} catch (IOException e) {
			Dialogs.error(frame, "Не удалось создать файл");
		}
	}

	public void openDatabase() {
		Path file = chooseFile(false);
		if (file == null || !confirmLock(file) || !closeCurrentDatabase()) {
			return;
		}

		char[] password = requestPassword(false);
		if (password == null) {
			return;
		}

		downloadRemoteThenOpen(file, password);
	}

	public void saveDatabase() {
		if (!database.isOpen()) {
			return;
		}

		frame.statusBar().setText("Сохранение файла...");
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				database.save();
				if (synchronizer != null) {
					synchronizer.save(database.file());
				}
				return null;
			}

			@Override
			protected void done() {
				frame.statusBar().clear();
				try {
					get();
				} catch (ExecutionException e) {
					showIoError(e.getCause(), "Не удалось записать данные в файл");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}.execute();
	}

	public void createAccount() {
		AccountDialog dialog = new AccountDialog(frame, "Новая запись", Account.empty());
		if (dialog.showDialog() == BaseDialog.OK_OPTION) {
			database.add(dialog.account());
			saveDatabase();
			frame.onDatabaseChanged();
		}
	}

	public void editAccount() {
		int row = frame.selectedRow();
		if (row == -1) {
			return;
		}

		AccountDialog dialog = new AccountDialog(frame, "Редактирование записи", database.get(row));
		if (dialog.showDialog() == BaseDialog.OK_OPTION) {
			database.update(row, dialog.account());
			saveDatabase();
			frame.onDatabaseChanged();
		}
	}

	public void removeAccount() {
		int row = frame.selectedRow();
		if (row == -1) {
			return;
		}

		database.remove(row);
		saveDatabase();
		frame.onDatabaseChanged();
	}

	public void importAccounts() {
		if (!database.isOpen()) {
			Dialogs.warning(frame, "Сначала откройте базу паролей");
			return;
		}

		ImportDialog dialog = new ImportDialog(frame);
		if (dialog.showDialog() != BaseDialog.OK_OPTION) {
			return;
		}

		try {
			List<Account> imported = importService.importAccounts(dialog.file(), dialog.format());
			imported.forEach(database::add);
			saveDatabase();
			frame.onDatabaseChanged();
		} catch (ImportException e) {
			Dialogs.error(frame, e.getMessage());
		}
	}

	public void editSettings() {
		SettingsDialog dialog = new SettingsDialog(frame, settings);
		if (dialog.showDialog() == BaseDialog.OK_OPTION) {
			applyLookAndFeel(true);
			synchronizer = SynchronizerFactory.create(settings.syncMethod()).orElse(null);
		}
	}

	public void exit() {
		frame.statusBar().setText("Сохранение файла...");
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				if (database.isOpen()) {
					database.save();
					if (synchronizer != null) {
						synchronizer.save(database.file());
					}
					database.close();
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
				} catch (ExecutionException e) {
					showIoError(e.getCause(), "Не удалось сохранить базу");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					System.exit(0);
				}
			}
		}.execute();
	}

	private void downloadRemoteThenOpen(Path file, char[] password) {
		if (synchronizer == null) {
			openFile(file, password);
			return;
		}

		frame.statusBar().setText("Загрузка из хранилища...");
		new SwingWorker<Optional<Path>, Void>() {
			@Override
			protected Optional<Path> doInBackground() throws Exception {
				return synchronizer.load(file);
			}

			@Override
			protected void done() {
				try {
					resolveRemote(file, get());
				} catch (ExecutionException | IOException e) {
					Dialogs.warning(frame, "Не удалось загрузить файл из хранилища");
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				openFile(file, password);
			}
		}.execute();
	}

	private void resolveRemote(Path file, Optional<Path> remote) throws IOException {
		if (remote.isEmpty()) {
			return;
		}

		Path remoteFile = remote.get();
		boolean useRemote = Files.mismatch(file, remoteFile) != -1
				&& Dialogs.confirm(frame, "Файл из хранилища и локальный файл не совпадают. "
				+ "Использовать файл из хранилища?");

		if (useRemote) {
			Files.move(remoteFile, file, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Files.deleteIfExists(remoteFile);
		}
	}

	private void openFile(Path file, char[] password) {
		frame.statusBar().setText("Чтение файла...");
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				database.open(file, password);
				return null;
			}

			@Override
			protected void done() {
				frame.statusBar().clear();
				try {
					get();
					frame.onDatabaseChanged();
				} catch (ExecutionException e) {
					showOpenError(e.getCause());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}.execute();
	}

	private void showOpenError(Throwable cause) {
		if (cause instanceof WrongPasswordException) {
			Dialogs.error(frame, "Неправильный пароль");
		} else if (cause instanceof CorruptDatabaseException) {
			Dialogs.error(frame, cause.getMessage());
		} else {
			Dialogs.error(frame, "Не удалось открыть файл");
		}
	}

	private void showIoError(Throwable cause, String fallback) {
		if (cause instanceof SyncException) {
			Dialogs.warning(frame, cause.getMessage());
		} else {
			Dialogs.error(frame, fallback);
		}
	}

	private boolean closeCurrentDatabase() {
		if (!database.isOpen()) {
			return true;
		}

		try {
			database.save();
			Path file = database.file();
			database.close();
			uploadInBackground(file);
			return true;
		} catch (IOException e) {
			Dialogs.error(frame, "Не удалось сохранить базу");
			return false;
		}
	}

	private void uploadInBackground(Path file) {
		if (synchronizer == null) {
			return;
		}
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				synchronizer.save(file);
				return null;
			}
		}.execute();
	}

	private boolean confirmLock(Path file) {
		return !database.isLocked(file)
				|| Dialogs.confirm(frame, "Файл либо уже открыт, либо программа была "
				+ "некорректно завершена. Всё равно открыть?");
	}

	private char[] requestPassword(boolean confirm) {
		PasswordDialog dialog = confirm
				? PasswordDialog.forCreate(frame)
				: PasswordDialog.forOpen(frame);

		if (dialog.showDialog() != BaseDialog.OK_OPTION) {
			return null;
		}

		char[] password = dialog.password();

		if (password.length == 0) {
			Dialogs.error(frame, "Введён пустой пароль");
			return null;
		}

		if (confirm && !dialog.passwordsMatch()) {
			Dialogs.error(frame, "Пароли не совпадают");
			return null;
		}

		return password;
	}

	private Path chooseFile(boolean save) {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		chooser.setFileFilter(new FileNameExtensionFilter(
				"Файлы баз паролей", Application.DATABASE_EXTENSION));

		int result = save ? chooser.showSaveDialog(frame) : chooser.showOpenDialog(frame);
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		}

		Path file = chooser.getSelectedFile().toPath();
		if (save && !file.toString().endsWith("." + Application.DATABASE_EXTENSION)) {
			file = file.resolveSibling(file.getFileName() + "." + Application.DATABASE_EXTENSION);
		}

		return file;
	}

	private void applyLookAndFeel(boolean updateComponents) {
		try {
			UIManager.setLookAndFeel(settings.theme());
			if (updateComponents) {
				SwingUtilities.updateComponentTreeUI(frame);
			}
		} catch (Exception e) {
			Dialogs.warning(frame, "Не удалось применить тему оформления");
		}
	}
}
