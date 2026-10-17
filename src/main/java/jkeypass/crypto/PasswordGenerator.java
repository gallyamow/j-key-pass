package jkeypass.crypto;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;

public final class PasswordGenerator {
	public enum CharSet {
		UPPERCASE("Прописные буквы", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
		LOWERCASE("Строчные буквы", "abcdefghijklmnopqrstuvwxyz"),
		DIGITS("Цифры", "0123456789"),
		SPECIAL("Специальные символы", "`~!@#$%^&*()_-+=<>.,/?|");

		private final String label;
		private final String symbols;

		CharSet(String label, String symbols) {
			this.label = label;
			this.symbols = symbols;
		}

		public String label() {
			return label;
		}

		public String symbols() {
			return symbols;
		}
	}

	private static final SecureRandom RANDOM = new SecureRandom();

	private PasswordGenerator() {
	}

	public static String generate(Set<CharSet> charSets, int length) {
		String alphabet = alphabet(charSets);

		StringBuilder password = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			password.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
		}

		return password.toString();
	}

	private static String alphabet(Set<CharSet> charSets) {
		if (charSets.isEmpty()) {
			throw new IllegalArgumentException("At least one character set must be selected");
		}

		StringBuilder alphabet = new StringBuilder();
		for (CharSet charSet : EnumSet.copyOf(charSets)) {
			alphabet.append(charSet.symbols());
		}

		return alphabet.toString();
	}
}
