package jkeypass.models;

import java.io.*;
import java.util.ArrayList;

public class AccountsDatabase {
	private File file;
	private ArrayList<Account> list = new ArrayList<Account>();

	public AccountsDatabase(File file) throws ClassNotFoundException, IOException {
		this.file = file;

		try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
			Account account;

			try {
				while ((account = (Account) objectStream.readObject()) != null) {
					this.add(account);
				}
			} catch (IOException e) {
				// todo: обработать
			}

			objectStream.close();
		}
	}

	public Account get(int index) {
		return this.list.get(index);
	}

	public int count() {
		return this.list.size();
	}

	public void add(Account account) {
		this.list.add(account);
	}

	public void update(int index, Account account) {
		this.list.set(index, account);
	}

	public void save() throws IOException {
		// очистка произойдет автоматически, так как FileOutputStream сконструирован с append = false
		try (ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(file))) {
			for (Account account : this.list) {
				objectStream.writeObject(account);
			}
			objectStream.close();
		}

	}
}
