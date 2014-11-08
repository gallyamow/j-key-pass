package jkeypass.models;

import java.io.Serializable;

public class Account implements Serializable {
	private String name;
	private String login;
	private String password;
	private String url;
	private String description;

	public Account() {
		this("", "", "", "", "");
	}

	public Account(String name, String login, String password, String url, String description) {
		this.name = name;
		this.login = login;
		this.password = password;
		this.url = url;
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public String getDescription() {
		return description;
	}
}
