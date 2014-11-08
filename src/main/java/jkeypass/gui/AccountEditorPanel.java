package jkeypass.gui;

import jkeypass.models.Account;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AccountEditorPanel extends JPanel {
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
	private Map<Field, JComponent> fieldMap = new HashMap<Field, JComponent>();

	private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
	private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

	public AccountEditorPanel(Account account) {
		this.account = account;

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
		this.setLayout(new GridBagLayout());

		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Аккаунт"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints gbc;

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

	private GridBagConstraints createGbc(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;

		gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
		gbc.fill = (x == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

		gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
		gbc.weightx = (x == 0) ? 0.1 : 1.0;
		gbc.weighty = 1.0;

		return gbc;
	}
}
