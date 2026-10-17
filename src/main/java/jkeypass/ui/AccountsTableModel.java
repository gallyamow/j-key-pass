package jkeypass.ui;

import jkeypass.domain.Account;
import jkeypass.service.DatabaseService;

import javax.swing.table.AbstractTableModel;
import java.util.function.Function;

public final class AccountsTableModel extends AbstractTableModel {
	public enum Column {
		NAME("Название", Account::name, false),
		LOGIN("Логин", Account::login, true),
		PASSWORD("Пароль", Account::password, true),
		URL("URL", Account::url, false);

		private static final String MASK = "*********";

		private final String label;
		private final Function<Account, String> extractor;
		private final boolean masked;

		Column(String label, Function<Account, String> extractor, boolean masked) {
			this.label = label;
			this.extractor = extractor;
			this.masked = masked;
		}

		public String value(Account account) {
			String value = extractor.apply(account);
			return masked && !value.isEmpty() ? MASK : value;
		}
	}

	private final DatabaseService database;

	public AccountsTableModel(DatabaseService database) {
		this.database = database;
	}

	@Override
	public int getRowCount() {
		return database.count();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return Column.values()[columnIndex].label;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.values()[columnIndex].value(database.get(rowIndex));
	}
}
