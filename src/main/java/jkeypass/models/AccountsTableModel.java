package jkeypass.models;

import javax.swing.table.AbstractTableModel;

public class AccountsTableModel extends AbstractTableModel {
	private AccountsDatabase database;

	private static final Column[] columns;

	static {
		columns = new Column[]{
				new Column("getName", "Название", false),
				new Column("getLogin", "Логин", true),
				new Column("getPassword", "Пароль", true),
				new Column("getUrl", "URL", false)
		};
	}

	public AccountsTableModel(AccountsDatabase database) {
		this.database = database;
	}

	@Override
	public int getRowCount() {
		return this.database.count();
	}

	@Override
	public int getColumnCount() {
		return AccountsTableModel.columns.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		Column column = AccountsTableModel.columns[columnIndex];

		return column.label;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Account account = this.database.get(rowIndex);

		Object result = null;

		if (account != null) {
			Column column = AccountsTableModel.columns[columnIndex];

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

	private static class Column {
		private Column(String method, String label, boolean hidden) {
			this.method = method;
			this.label = label;
			this.hidden = hidden;
		}

		private String method;
		private String label;
		private boolean hidden;
	}
}
