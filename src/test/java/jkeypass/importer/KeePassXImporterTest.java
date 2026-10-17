package jkeypass.importer;

import jkeypass.domain.Account;
import jkeypass.importer.keepassx.KeePassXImporter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KeePassXImporterTest {
	private final KeePassXImporter importer = new KeePassXImporter();

	@TempDir
	Path dir;

	@Test
	void parsesEntries() throws Exception {
		Path file = writeXml("""
				<database>
					<group>
						<entry>
							<title>Mail</title>
							<username>user</username>
							<password>secret</password>
							<url>https://mail.com</url>
							<comment>note</comment>
						</entry>
						<entry>
							<title>Bank</title>
							<username>client</username>
							<password>pw</password>
						</entry>
					</group>
				</database>
				""");

		List<Account> accounts = importer.importAccounts(file);

		assertEquals(2, accounts.size());
		assertEquals(new Account("Mail", "user", "secret", "https://mail.com", "note"), accounts.get(0));
		assertEquals(new Account("Bank", "client", "pw", "", ""), accounts.get(1));
	}

	@Test
	void rejectsNonXml() throws Exception {
		Path file = writeXml("not xml at all");

		assertThrows(ImportException.class, () -> importer.importAccounts(file));
	}

	@Test
	void rejectsXxe() throws Exception {
		Path file = writeXml("""
				<?xml version="1.0"?>
				<!DOCTYPE database [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
				<database><entry><title>&xxe;</title></entry></database>
				""");

		assertThrows(ImportException.class, () -> importer.importAccounts(file));
	}

	private Path writeXml(String content) throws Exception {
		Path file = dir.resolve("import.xml");
		Files.writeString(file, content);
		return file;
	}
}
