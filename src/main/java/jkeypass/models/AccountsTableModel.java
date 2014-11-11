package jkeypass.models;

import javax.swing.table.AbstractTableModel;

public class AccountsTableModel extends AbstractTableModel {
	public enum Column {
		NAME("Название", "getName", false),
		LOGIN("Логин", "getLogin", true),
		PASSWORD("Пароль", "getPassword", true),
		URL("URL", "getUrl", false);

		private String method;
		private String label;
		private boolean hidden;

		private Column(String label, String method, boolean hidden) {
			this.label = label;
			this.method = method;
			this.hidden = hidden;
		}
	}

	private AccountsDatabase database;

	public AccountsTableModel(AccountsDatabase database) {
		this.database = database;
	}

	public int getDatabaseIndexByRowIndex(int rowIndex) {
		return rowIndex;
	}

	@Override
	public int getRowCount() {
		return this.database.count();
	}

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		Column column = Column.values()[columnIndex];

		return column.label;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Account account = this.database.get(rowIndex);

		Object result = null;

		if (account != null) {
			Column column = Column.values()[columnIndex];

			if (column.hidden) {
				result = "********";
			} else {
				try {
					result = Account.class.getMethod(column.method).invoke(account);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
