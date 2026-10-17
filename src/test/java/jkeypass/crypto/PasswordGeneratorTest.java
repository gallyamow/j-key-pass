package jkeypass.crypto;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorTest {
	@Test
	void generatesRequestedLength() {
		String password = PasswordGenerator.generate(EnumSet.allOf(PasswordGenerator.CharSet.class), 25);

		assertEquals(25, password.length());
	}

	@Test
	void usesOnlySelectedCharSets() {
		String password = PasswordGenerator.generate(EnumSet.of(PasswordGenerator.CharSet.DIGITS), 20);

		assertTrue(password.chars().allMatch(Character::isDigit));
	}

	@Test
	void rejectsEmptySelection() {
		assertThrows(IllegalArgumentException.class,
				() -> PasswordGenerator.generate(EnumSet.noneOf(PasswordGenerator.CharSet.class), 10));
	}
}
