package jkeypass.importation;

import jkeypass.models.Account;

import java.io.File;
import java.util.ArrayList;

public interface Parser {
	public ArrayList<Account> getList(File file) throws ImportException;
}
