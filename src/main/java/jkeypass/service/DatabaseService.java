package jkeypass.service;

import jkeypass.crypto.CryptoService;
import jkeypass.domain.Account;
import jkeypass.repository.AccountRepository;
import jkeypass.repository.CorruptDatabaseException;
import jkeypass.repository.DatabaseContent;
import jkeypass.repository.EncryptedFileAccountRepository;
import jkeypass.repository.WrongPasswordException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the state of the currently open password database:
 * the decrypted accounts, the key material and the lock file.
 */
public final class DatabaseService {
	private final AccountRepository repository;

	private Path file;
	private CryptoService crypto;
	private List<Account> accounts = new ArrayList<>();
	private boolean open;

	public DatabaseService() {
		this(new EncryptedFileAccountRepository());
	}

	public DatabaseService(AccountRepository repository) {
		this.repository = repository;
	}

	public boolean isOpen() {
		return open;
	}

	public Path file() {
		return file;
	}

	public List<Account> accounts() {
		return Collections.unmodifiableList(accounts);
	}

	public Account get(int index) {
		return accounts.get(index);
	}

	public int count() {
		return accounts.size();
	}

	public void add(Account account) {
		accounts.add(account);
	}

	public void update(int index, Account account) {
		accounts.set(index, account);
	}

	public void remove(int index) {
		accounts.remove(index);
	}

	public boolean isLocked(Path file) {
		return Files.exists(lockPath(file));
	}

	/**
	 * Creates an empty database at the given path. Consumes and wipes the password.
	 */
	public void create(Path file, char[] password) throws IOException {
		crypto = CryptoService.create(
				password, CryptoService.randomBytes(CryptoService.SALT_LENGTH), CryptoService.ITERATIONS);
		accounts = new ArrayList<>();
		this.file = file;

		save();
		lock();
		open = true;
	}

	/**
	 * Opens an existing database. Consumes and wipes the password.
	 */
	public void open(Path file, char[] password)
			throws IOException, CorruptDatabaseException, WrongPasswordException {
		DatabaseContent content = repository.load(file, password);

		crypto = content.crypto();
		accounts = new ArrayList<>(content.accounts());
		this.file = file;

		lock();
		open = true;
	}

	public void save() throws IOException {
		repository.save(file, accounts, crypto);
	}

	public void close() throws IOException {
		open = false;
		accounts.clear();
		crypto = null;
		unlock();
	}

	private Path lockPath(Path file) {
		return file.resolveSibling(file.getFileName() + ".lock");
	}

	private void lock() throws IOException {
		Path lockFile = lockPath(file);
		Files.deleteIfExists(lockFile);
		Files.createFile(lockFile);
		lockFile.toFile().deleteOnExit();
	}

	private void unlock() throws IOException {
		if (file != null) {
			Files.deleteIfExists(lockPath(file));
		}
	}
}
