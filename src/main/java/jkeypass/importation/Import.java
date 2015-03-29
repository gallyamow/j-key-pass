package jkeypass.importation;

import jkeypass.importation.keepassx.KeePassXParser;
import jkeypass.models.Account;
import jkeypass.models.AccountsDatabase;

import java.io.File;
import java.util.ArrayList;

public class Import {
	public enum Source {
		KEEPASSX
	}

	private AccountsDatabase database;
	private File file;
	private Source source;

	public Import(AccountsDatabase database, File file, Source source) {
		this.database = database;
		this.file = file;
		this.source = source;
	}

	public void doImport() throws ImportException {
		Parser parser = getParser(source);

		ArrayList<Account> list = parser.getList(file);

		for (Account account : list) {
			database.add(account);
		}
	}

	private Parser getParser(Source source) {
		switch (source) {
			case KEEPASSX:
				return new KeePassXParser();
		}

		return null;
	}
}
