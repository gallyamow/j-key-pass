package jkeypass.service;

import jkeypass.domain.Account;
import jkeypass.importer.ImportException;
import jkeypass.importer.ImportFormat;

import java.nio.file.Path;
import java.util.List;

public final class ImportService {
	public List<Account> importAccounts(Path file, ImportFormat format) throws ImportException {
		return format.newImporter().importAccounts(file);
	}
}
