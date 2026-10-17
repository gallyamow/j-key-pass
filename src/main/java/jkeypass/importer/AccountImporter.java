package jkeypass.importer;

import jkeypass.domain.Account;

import java.nio.file.Path;
import java.util.List;

public interface AccountImporter {
	List<Account> importAccounts(Path file) throws ImportException;
}
