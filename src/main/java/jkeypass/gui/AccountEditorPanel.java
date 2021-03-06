package jkeypass.gui;

import jkeypass.common.GridBagLayoutHelper;
import jkeypass.common.Resources;
import jkeypass.models.Account;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
			return label;
		}
	}

	enum SymbolType {
		UPPERCASE("Прописные буквы", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
		LOWERCASE("Строчные буквы буквы", "abcdefghijklmnopqrstuvwxyz"),
		NUMERALS("Цифры", "0123456789"),
		SPECIALCHARS("Специальные символы", "`~!@#$%^&*()_-+=<>.,/?|");

		private String name;
		private String symbols;

		private SymbolType(String name, String symbols) {
			this.name = name;
			this.symbols = symbols;
		}

		public String getSymbols() {
			return symbols;
		}
	}

	private Account account;
	private Map<Field, JComponent> fieldMap = new HashMap<>();

	public AccountEditorPanel(Account account) {
		super();

		setLayout(new GridBagLayout());

		this.account = account;

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Аккаунт"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		inputs();
	}

	public Account getUpdatedAccount() {
		for (Field field : Field.values()) {
			JComponent input = fieldMap.get(field);

			String value = "";

			if (input instanceof JTextComponent) {
				value = ((JTextComponent) input).getText();
			}

			setAccountProperty(field.setter, value);
		}

		return account;
	}

	private void inputs() {
		int i = 0;

		for (Field field : new Field[]{Field.NAME, Field.URL, Field.LOGIN}) {
			add(new JLabel(field.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));

			JTextField inputField = new JTextField((String) getAccountProperty(field.getter), 10);
			add(inputField, GridBagLayoutHelper.gbc(1, i));

			fieldMap.put(field, inputField);

			i++;
		}

		Field field;

		i++;
		field = Field.PASSWORD;

		add(new JLabel(field.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));

		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JPasswordField passwordField = new JPasswordField((String) getAccountProperty(field.getter), 30);
		passwordPanel.add(passwordField);

		JButton showPasswordButton = new JButton("Показать пароль", (new Resources()).getIcon("show-password.png"));
		showPasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPasswordField passwordField = (JPasswordField) fieldMap.get(Field.PASSWORD);

				if (passwordField.getEchoChar() == ((char) 0)) {
					setPasswordVisiblity(passwordField, false);
				} else {
					setPasswordVisiblity(passwordField, true);
				}
			}
		});

		passwordPanel.add(showPasswordButton);
		add(passwordPanel, GridBagLayoutHelper.gbc(1, i));

		fieldMap.put(field, passwordField);

		i++;
		GridBagConstraints gbc = new GridBagConstraints(0, i, 2, 1, 0.1, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0);
		JPanel generator = passwordGeneratorPanel();
		add(generator, gbc);

		i++;
		field = Field.DESCRIPTION;

		add(new JLabel(field.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));

		JTextArea textArea = new JTextArea((String) getAccountProperty(field.getter), 6, 20);
		textArea.setLineWrap(true);
		add(new JScrollPane(textArea), GridBagLayoutHelper.gbc(1, i));

		fieldMap.put(field, textArea);
	}

	private JPanel passwordGeneratorPanel() {
		final Map<SymbolType, JCheckBox> symbolsCheckboxes = new HashMap<>();

		JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		for (SymbolType type : SymbolType.values()) {
			JCheckBox checkbox = new JCheckBox(type.name, true);
			typesPanel.add(checkbox);

			symbolsCheckboxes.put(type, checkbox);
		}

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

		final JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 30, 1));
		lengthSpinner.setValue(15);
		buttonsPanel.add(lengthSpinner);

		JButton randPasswordButton = new JButton("Генерировать пароль", (new Resources()).getIcon("generate-password.png"));
		buttonsPanel.add(randPasswordButton);

		randPasswordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();

				for (SymbolType type : SymbolType.values()) {
					JCheckBox checkbox = symbolsCheckboxes.get(type);

					if (checkbox.isSelected()) {
						sb.append(type.getSymbols());
					}
				}

				char[] symbols = sb.toString().toCharArray();

				sb = new StringBuilder();
				Random rand = new Random();

				if (symbols.length > 0) {
					for (int i = 0; i < (int) lengthSpinner.getValue(); i++) {
						sb.append(symbols[rand.nextInt(symbols.length)]);
					}

					String password = sb.toString();

					JPasswordField passwordField = (JPasswordField) fieldMap.get(Field.PASSWORD);

					setPasswordVisiblity(passwordField, true);
					passwordField.setText(password);
				}
			}
		});

		JPanel panel = new JPanel(new GridLayout(2, 1));

		panel.add(typesPanel);
		panel.add(buttonsPanel);

		return panel;
	}

	private Object getAccountProperty(String getter) {
		Object result = null;

		try {
			result = Account.class.getMethod(getter).invoke(account);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private void setAccountProperty(String setter, Object value) {
		try {
			Account.class.getMethod(setter, value.getClass()).invoke(account, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPasswordVisiblity(JPasswordField passwordField, boolean visibility) {
		if (visibility) {
			passwordField.setEchoChar((char) 0);
		} else {
			passwordField.setEchoChar('*');
		}
	}
}
