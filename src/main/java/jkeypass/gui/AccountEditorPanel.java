package jkeypass.gui;

import jkeypass.models.Account;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AccountEditorPanel extends GridBagPanel {
	enum Field {
		NAME("Название", "getName", "setName"),
		LOGIN("Логин", "getLogin", "setLogin"),
		PASSWORD("Пароль", "getPassword", "setPassword"),
		URL("URL", "getUrl", "setUrl"),
		DESCRIPTION("Дополнительно", "getDescription", "setDescription");

		private String label;
		private String getter;
		private String setter;

		private Field(String label, String getter, String setter) {
			this.label = label;
			this.getter = getter;
			this.setter = setter;
		}

		public String getLabel() {
			return this.label;
		}
	}

	private Account account;
	private Map<Field, JComponent> fieldMap = new HashMap<>();

	public AccountEditorPanel(Account account) {
		this.account = account;

		this.setLayout(new GridBagLayout());

		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Аккаунт"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		this.createInputs();
	}

	public Account getUpdatedAccount() {
		for (Field field : Field.values()) {
			JComponent input = this.fieldMap.get(field);

			String value = "";

			if (input instanceof JTextComponent) {
				value = ((JTextComponent) input).getText();
			}

			this.setAccountProperty(field.setter, value);
		}

		return this.account;
	}

	private void createInputs() {
		int i = 0;

		for (Field field : new Field[]{Field.NAME, Field.URL, Field.LOGIN}) {
			this.add(new JLabel(field.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));

			JTextField inputField = new JTextField((String) this.getAccountProperty(field.getter), 10);
			this.add(inputField, this.createGbc(1, i));

			this.fieldMap.put(field, inputField);

			i++;
		}

		Field field;

		i++;
		field = Field.PASSWORD;

		this.add(new JLabel(field.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));

		JPasswordField passwordField = new JPasswordField((String) this.getAccountProperty(field.getter), 10);
		this.add(passwordField, this.createGbc(1, i));

		this.fieldMap.put(field, passwordField);

		i++;
		field = Field.DESCRIPTION;

		this.add(new JLabel(field.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));

		JTextArea textArea = new JTextArea((String) this.getAccountProperty(field.getter), 6, 20);
		textArea.setLineWrap(true);
		this.add(new JScrollPane(textArea), this.createGbc(1, i));

		this.fieldMap.put(field, textArea);
	}

	private Object getAccountProperty(String getter) {
		Object result = null;

		try {
			result = Account.class.getMethod(getter).invoke(this.account);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private void setAccountProperty(String setter, Object value) {
		try {
			Account.class.getMethod(setter, value.getClass()).invoke(this.account, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
