package jkeypass.domain;

public record Account(String name, String login, String password, String url, String description) {

	public Account {
		name = name == null ? "" : name;
		login = login == null ? "" : login;
		password = password == null ? "" : password;
		url = url == null ? "" : url;
		description = description == null ? "" : description;
	}

	public static Account empty() {
		return new Account("", "", "", "", "");
	}
}
