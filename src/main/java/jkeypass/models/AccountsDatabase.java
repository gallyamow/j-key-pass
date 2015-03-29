package jkeypass.models;

import jkeypass.common.Crypto;
import jkeypass.sync.SyncException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.util.ArrayList;

public class AccountsDatabase {
	private File file;
	private File lockFile;
	private boolean open = false;
	private Crypto crypto;

	private ArrayList<Account> list = new ArrayList<>();

	public AccountsDatabase(File file, Crypto crypto) {
		this.file = file;
		this.crypto = crypto;

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
			try (
					BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
					CipherInputStream cis = new CipherInputStream(is, crypto.getCipher(Cipher.DECRYPT_MODE));
					ObjectInputStream stream = new ObjectInputStream(cis)
			) {
				Account account;

				try {
					while ((account = (Account) stream.readObject()) != null) {
						add(account);
					}
				} catch (IOException ignored) {
				}

				stream.close();
			}
		}

		lock();
		open = true;
	}

	public void save() throws IOException, SyncException {
		if (open) {
			// очистка произойдет автоматически, так как FileOutputStream сконструирован с append = false
			try (
					BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
					CipherOutputStream cos = new CipherOutputStream(os, crypto.getCipher(Cipher.ENCRYPT_MODE));
					ObjectOutputStream stream = new ObjectOutputStream(cos)
			) {
				for (Account account : list) {
					stream.writeObject(account);


				}

				stream.close();
			}
		}
	}

	public void close() throws IOException {
		unlock();
	}

	public boolean isLocked() {
		return lockFile.exists();
	}

	private boolean lock() throws IOException {
		return lockFile.createNewFile();
	}

	private boolean unlock() throws IOException {
		return lockFile.delete();
	}
}
