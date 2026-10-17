package jkeypass.ui.panel;

import jkeypass.crypto.PasswordGenerator;
import jkeypass.crypto.PasswordGenerator.CharSet;
import jkeypass.domain.Account;
import jkeypass.ui.util.GridBagLayoutHelper;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class AccountEditorPanel extends JPanel {
	private final JTextField nameField;
	private final JTextField loginField;
	private final JPasswordField passwordField;
	private final JTextField urlField;
	private final JTextArea descriptionArea;

	public AccountEditorPanel(Account account) {
		super(new GridBagLayout());

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Аккаунт"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		nameField = new JTextField(account.name(), 10);
		urlField = new JTextField(account.url(), 10);
		loginField = new JTextField(account.login(), 10);
		passwordField = new JPasswordField(account.password(), 30);
		descriptionArea = new JTextArea(account.description(), 6, 20);

		int row = 0;
		row = addRow(row, "Название:", nameField);
		row = addRow(row, "URL:", urlField);
		row = addRow(row, "Логин:", loginField);
		row = addPasswordRow(row);

		add(passwordGeneratorPanel(), fullWidthConstraints(++row));

		descriptionArea.setLineWrap(true);
		row = addRow(++row, "Дополнительно:", new JScrollPane(descriptionArea));
	}

	public Account account() {
		return new Account(
				nameField.getText(),
				loginField.getText(),
				new String(passwordField.getPassword()),
				urlField.getText(),
				descriptionArea.getText());
	}

	private int addRow(int row, String label, JComponent field) {
		add(new JLabel(label, JLabel.LEFT), GridBagLayoutHelper.gbc(0, row));
		add(field, GridBagLayoutHelper.gbc(1, row));
		return row + 1;
	}

	private int addPasswordRow(int row) {
		add(new JLabel("Пароль:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, row));

		JButton showPasswordButton = new JButton("Показать пароль", Resources.icon("show-password.png"));
		showPasswordButton.addActionListener(e -> setPasswordVisible(passwordField.getEchoChar() != (char) 0));

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		panel.add(passwordField);
		panel.add(showPasswordButton);

		add(panel, GridBagLayoutHelper.gbc(1, row));

		return row;
	}

	private JPanel passwordGeneratorPanel() {
		Map<CharSet, JCheckBox> checkboxes = new HashMap<>();

		JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		for (CharSet charSet : CharSet.values()) {
			JCheckBox checkbox = new JCheckBox(charSet.label(), true);
			typesPanel.add(checkbox);
			checkboxes.put(charSet, checkbox);
		}

		JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 30, 1));

		JButton generateButton = new JButton("Генерировать пароль", Resources.icon("generate-password.png"));
		generateButton.addActionListener(e -> {
			Set<CharSet> selected = EnumSet.noneOf(CharSet.class);
			checkboxes.forEach((charSet, checkbox) -> {
				if (checkbox.isSelected()) {
					selected.add(charSet);
				}
			});

			if (!selected.isEmpty()) {
				String password = PasswordGenerator.generate(selected, (int) lengthSpinner.getValue());
				setPasswordVisible(true);
				passwordField.setText(password);
			}
		});

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonsPanel.add(lengthSpinner);
		buttonsPanel.add(generateButton);

		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(typesPanel);
		panel.add(buttonsPanel);

		return panel;
	}

	private GridBagConstraints fullWidthConstraints(int row) {
		return new GridBagConstraints(0, row, 2, 1, 0.1, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 0, 5, 5), 0, 0);
	}

	private void setPasswordVisible(boolean visible) {
		passwordField.setEchoChar(visible ? (char) 0 : '*');
	}
}
