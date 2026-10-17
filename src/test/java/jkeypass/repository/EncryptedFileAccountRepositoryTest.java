package jkeypass.repository;

import jkeypass.crypto.CryptoService;
import jkeypass.domain.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EncryptedFileAccountRepositoryTest {
	private final EncryptedFileAccountRepository repository = new EncryptedFileAccountRepository();

	@TempDir
	Path dir;

	@Test
	void saveAndLoadRoundTrip() throws Exception {
		Path file = dir.resolve("db.jkp");
		List<Account> accounts = List.of(
				new Account("mail", "user@mail.com", "s3cret", "https://mail.com", "личная"),
				new Account("bank", "client", "p@ss", "", ""));

		CryptoService crypto = newCrypto("password".toCharArray());
		repository.save(file, accounts, crypto);

		DatabaseContent content = repository.load(file, "password".toCharArray());

		assertEquals(accounts, content.accounts());
	}

	@Test
	void loadWithWrongPasswordFails() throws Exception {
		Path file = dir.resolve("db.jkp");
		repository.save(file, List.of(Account.empty()), newCrypto("right".toCharArray()));

		assertThrows(WrongPasswordException.class,
				() -> repository.load(file, "wrong".toCharArray()));
	}

	@Test
	void tamperedCiphertextFailsIntegrityCheck() throws Exception {
		Path file = dir.resolve("db.jkp");
		repository.save(file, List.of(Account.empty()), newCrypto("pw".toCharArray()));

		byte[] data = Files.readAllBytes(file);
		data[data.length - 1] ^= 0xFF;
		Files.write(file, data);

		assertThrows(WrongPasswordException.class,
				() -> repository.load(file, "pw".toCharArray()));
	}

	@Test
	void foreignFileIsRejected() throws Exception {
		Path file = dir.resolve("db.jkp");
		Files.write(file, "not a database".getBytes());

		assertThrows(CorruptDatabaseException.class,
				() -> repository.load(file, "pw".toCharArray()));
	}

	@Test
	void emptyFileLoadsAsEmptyDatabase() throws Exception {
		Path file = dir.resolve("db.jkp");
		Files.createFile(file);

		DatabaseContent content = repository.load(file, "pw".toCharArray());

		assertEquals(0, content.accounts().size());
	}

	private CryptoService newCrypto(char[] password) {
		return CryptoService.create(
				password, CryptoService.randomBytes(CryptoService.SALT_LENGTH), CryptoService.ITERATIONS);
	}
}
