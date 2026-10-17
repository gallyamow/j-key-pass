package jkeypass.crypto;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Key material derived from the master password.
 * PBKDF2 produces 512 bits: first half is the AES-256 key, second half is the HMAC key.
 */
public final class CryptoService {
	public static final int ITERATIONS = 210_000;
	public static final int SALT_LENGTH = 16;
	public static final int IV_LENGTH = 16;
	public static final int MAC_LENGTH = 32;

	private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final SecureRandom RANDOM = new SecureRandom();

	private final byte[] salt;
	private final int iterations;
	private final SecretKey aesKey;
	private final SecretKey macKey;

	private CryptoService(byte[] salt, int iterations, SecretKey aesKey, SecretKey macKey) {
		this.salt = salt;
		this.iterations = iterations;
		this.aesKey = aesKey;
		this.macKey = macKey;
	}

	/**
	 * Derives key material from the password. The passed array is wiped —
	 * do not use it after this call.
	 */
	public static CryptoService create(char[] password, byte[] salt, int iterations) {
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, 512);
			byte[] material = factory.generateSecret(spec).getEncoded();

			SecretKey aes = new SecretKeySpec(material, 0, 32, "AES");
			SecretKey mac = new SecretKeySpec(material, 32, 32, "HmacSHA256");
			Arrays.fill(material, (byte) 0);

			return new CryptoService(salt, iterations, aes, mac);
		} catch (GeneralSecurityException e) {
			throw new CryptoException(e);
		} finally {
			if (password != null) {
				Arrays.fill(password, '\0');
			}
		}
	}

	public byte[] salt() {
		return salt;
	}

	public int iterations() {
		return iterations;
	}

	public Cipher cipher(int mode, byte[] iv) {
		try {
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(mode, aesKey, new IvParameterSpec(iv));
			return cipher;
		} catch (GeneralSecurityException e) {
			throw new CryptoException(e);
		}
	}

	public Mac mac() {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(macKey);
			return mac;
		} catch (GeneralSecurityException e) {
			throw new CryptoException(e);
		}
	}

	public static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		RANDOM.nextBytes(bytes);
		return bytes;
	}
}
