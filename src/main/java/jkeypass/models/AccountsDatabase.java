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
		this.lockFile = new File(this.file.getParent(), this.file.getName() + ".lock");
	}

	public File getFile() {
		return this.file;
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

	public void remove(int index) {
		this.list.remove(index);
	}

	public boolean isOpen() {
		return open;
	}

	public void open() throws IOException, ClassNotFoundException {
		if (file.length() > 0) {
			try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(this.file)))) {
				Account account;

				try {
					while ((account = (Account) objectStream.readObject()) != null) {
						this.add(account);
					}
				} catch (IOException e) {
					// e.printStackTrace(); todo: корректно обработать
				}

				objectStream.close();
			}
		}

		this.lock();
		this.open = true;
	}

	public void save() throws IOException, SyncException {
		if (this.open) {
			// очистка произойдет автоматически, так как FileOutputStream сконструирован с append = false
			try (ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(this.file))) {
				for (Account account : this.list) {
					objectStream.writeObject(account);
				}
				objectStream.close();
			}
		}
	}

	public void close() throws IOException {
		this.unlock();
	}

	public boolean isLocked() {
		return this.lockFile.exists();
	}

	private void lock() throws IOException {
		this.lockFile.createNewFile();
	}

	private void unlock() throws IOException {
		this.lockFile.delete();
	}
}
