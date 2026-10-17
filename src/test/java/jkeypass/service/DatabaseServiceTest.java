package jkeypass.service;

import jkeypass.domain.Account;
import jkeypass.repository.WrongPasswordException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseServiceTest {
	private final DatabaseService database = new DatabaseService();

	@TempDir
	Path dir;

	@Test
	void createOpenAndModify() throws Exception {
		Path file = dir.resolve("db.jkp");

		database.create(file, "pw".toCharArray());
		assertTrue(database.isOpen());
		assertTrue(database.isLocked(file));

		database.add(new Account("a", "b", "c", "d", "e"));
		database.save();
		database.close();
		assertFalse(database.isLocked(file));

		DatabaseService reopened = new DatabaseService();
		reopened.open(file, "pw".toCharArray());
		assertEquals(1, reopened.count());
		assertEquals("a", reopened.get(0).name());
	}

	@Test
	void openWithWrongPasswordFails() throws Exception {
		Path file = dir.resolve("db.jkp");
		database.create(file, "pw".toCharArray());
		database.close();

		DatabaseService other = new DatabaseService();
		assertThrows(WrongPasswordException.class, () -> other.open(file, "bad".toCharArray()));
		assertFalse(other.isOpen());
	}

	@Test
	void lockFileIsRemovedOnClose() throws Exception {
		Path file = dir.resolve("db.jkp");
		database.create(file, "pw".toCharArray());
		Path lock = file.resolveSibling("db.jkp.lock");
		assertTrue(Files.exists(lock));

		database.close();
		assertFalse(Files.exists(lock));
	}
}
