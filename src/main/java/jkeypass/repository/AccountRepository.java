package jkeypass.repository;

import jkeypass.crypto.CryptoService;
import jkeypass.domain.Account;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AccountRepository {
	/**
	 * Reads and decrypts the database file. Returns the stored accounts together
	 * with the {@link CryptoService} derived from the password and the file header,
	 * so subsequent saves reuse the same salt.
	 */
	DatabaseContent load(Path file, char[] password)
			throws IOException, CorruptDatabaseException, WrongPasswordException;

	void save(Path file, List<Account> accounts, CryptoService crypto) throws IOException;
}
