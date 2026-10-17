package jkeypass.repository;

import jkeypass.crypto.CryptoException;
import jkeypass.crypto.CryptoService;
import jkeypass.domain.Account;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Database file format:
 * <pre>
 *   4 bytes   magic "JKP2"
 *   4 bytes   KDF iterations
 *   16 bytes  KDF salt
 *   16 bytes  AES-CBC IV
 *   32 bytes  HMAC-SHA256 over (header + ciphertext)
 *   N bytes   ciphertext
 * </pre>
 * The plaintext payload is a sequence of accounts: record count, then for each
 * account five length-prefixed UTF-8 strings.
 */
public final class EncryptedFileAccountRepository implements AccountRepository {
	private static final byte[] MAGIC = {'J', 'K', 'P', '2'};
	private static final int HEADER_LENGTH =
			MAGIC.length + Integer.BYTES + CryptoService.SALT_LENGTH + CryptoService.IV_LENGTH;

	@Override
	public DatabaseContent load(Path file, char[] password)
			throws IOException, CorruptDatabaseException, WrongPasswordException {
		byte[] data = Files.readAllBytes(file);

		if (data.length == 0) {
			CryptoService crypto = CryptoService.create(
					password, CryptoService.randomBytes(CryptoService.SALT_LENGTH), CryptoService.ITERATIONS);
			return new DatabaseContent(List.of(), crypto);
		}

		if (data.length < HEADER_LENGTH + CryptoService.MAC_LENGTH) {
			throw new CorruptDatabaseException("Файл повреждён или не является базой паролей");
		}

		ByteBuffer buffer = ByteBuffer.wrap(data);
		byte[] magic = new byte[MAGIC.length];
		buffer.get(magic);
		if (!MessageDigest.isEqual(magic, MAGIC)) {
			throw new CorruptDatabaseException("Файл повреждён или не является базой паролей");
		}

		int iterations = buffer.getInt();
		byte[] salt = new byte[CryptoService.SALT_LENGTH];
		byte[] iv = new byte[CryptoService.IV_LENGTH];
		byte[] storedMac = new byte[CryptoService.MAC_LENGTH];
		buffer.get(salt).get(iv).get(storedMac);

		byte[] ciphertext = new byte[buffer.remaining()];
		buffer.get(ciphertext);

		CryptoService crypto = CryptoService.create(password, salt, iterations);

		Mac mac = crypto.mac();
		mac.update(data, 0, HEADER_LENGTH);
		byte[] computedMac = mac.doFinal(ciphertext);
		if (!MessageDigest.isEqual(storedMac, computedMac)) {
			throw new WrongPasswordException();
		}

		try {
			byte[] plaintext = crypto.cipher(Cipher.DECRYPT_MODE, iv).doFinal(ciphertext);
			return new DatabaseContent(deserialize(plaintext), crypto);
		} catch (CryptoException | CorruptDatabaseException e) {
			throw e;
		} catch (Exception e) {
			throw new CorruptDatabaseException("Не удалось расшифровать базу паролей");
		}
	}

	@Override
	public void save(Path file, List<Account> accounts, CryptoService crypto) throws IOException {
		byte[] iv = CryptoService.randomBytes(CryptoService.IV_LENGTH);

		byte[] ciphertext;
		try {
			ciphertext = crypto.cipher(Cipher.ENCRYPT_MODE, iv).doFinal(serialize(accounts));
		} catch (Exception e) {
			throw new IOException("Не удалось зашифровать базу паролей", e);
		}

		ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH);
		header.put(MAGIC).putInt(crypto.iterations()).put(crypto.salt()).put(iv);

		Mac mac = crypto.mac();
		mac.update(header.array());
		byte[] tag = mac.doFinal(ciphertext);

		Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
		Files.write(tempFile, concat(header.array(), tag, ciphertext));

		try {
			Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (AtomicMoveNotSupportedException e) {
			Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private byte[] serialize(List<Account> accounts) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);

		out.writeInt(accounts.size());
		for (Account account : accounts) {
			writeString(out, account.name());
			writeString(out, account.login());
			writeString(out, account.password());
			writeString(out, account.url());
			writeString(out, account.description());
		}
		out.flush();

		return bytes.toByteArray();
	}

	private List<Account> deserialize(byte[] plaintext) throws CorruptDatabaseException {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(plaintext));

			int count = in.readInt();
			List<Account> accounts = new ArrayList<>(count);

			for (int i = 0; i < count; i++) {
				accounts.add(new Account(
						readString(in), readString(in), readString(in), readString(in), readString(in)));
			}

			return accounts;
		} catch (IOException | NegativeArraySizeException e) {
			throw new CorruptDatabaseException("Не удалось прочитать базу паролей");
		}
	}

	private void writeString(DataOutputStream out, String value) throws IOException {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		out.writeInt(bytes.length);
		out.write(bytes);
	}

	private String readString(DataInputStream in) throws IOException {
		int length = in.readInt();
		byte[] bytes = in.readNBytes(length);
		if (bytes.length != length) {
			throw new IOException("Unexpected end of stream");
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private byte[] concat(byte[]... parts) {
		int total = 0;
		for (byte[] part : parts) {
			total += part.length;
		}

		ByteBuffer buffer = ByteBuffer.allocate(total);
		for (byte[] part : parts) {
			buffer.put(part);
		}

		return buffer.array();
	}
}
