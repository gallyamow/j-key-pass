package jkeypass.repository;

public class WrongPasswordException extends Exception {
	public WrongPasswordException() {
		super("Неправильный пароль");
	}
}
