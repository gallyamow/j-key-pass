package jkeypass.models;

import jkeypass.sync.SyncException;

import java.io.*;
import java.util.ArrayList;

public class AccountsDatabase {
	private File file;
	private File lockFile;
	private boolean open = false;

	private ArrayList<Account> list = new ArrayList<>();

	public AccountsDatabase(File file) {
		this.file = file;
		lockFile = new File(file.getParent(), file.getName() + ".lock");
	}

	public File getFile() {
		return file;
	}

	public Account get(int index) {
		return list.get(index);
	}

	public int count() {
		return list.size();
	}

	public void add(Account account) {
		list.add(account);
	}

	public void update(int index, Account account) {
		list.set(index, account);
	}

	public void remove(int index) {
		list.remove(index);
	}

	public boolean isOpen() {
		return open;
	}

	public void open() throws IOException, ClassNotFoundException {
		if (file.length() > 0) {
			try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
				Account account;

				try {
					while ((account = (Account) objectStream.readObject()) != null) {
						add(account);
					}
				} catch (IOException e) {
					// e.printStackTrace(); todo: корректно обработать
				}

				objectStream.close();
			}
		}

		lock();
		open = true;
	}

	public void save() throws IOException, SyncException {
		if (open) {
			// очистка произойдет автоматически, так как FileOutputStream сконструирован с append = false
			try (ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(file))) {
				for (Account account : list) {
					objectStream.writeObject(account);
				}
				objectStream.close();
			}
		}
	}

	public void close() throws IOException {
		unlock();
	}

	public boolean isLocked() {
		return lockFile.exists();
	}

	private void lock() throws IOException {
		lockFile.createNewFile();
	}

	private void unlock() throws IOException {
		lockFile.delete();
	}
}
