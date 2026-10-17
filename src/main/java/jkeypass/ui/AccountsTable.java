package jkeypass.ui;

import jkeypass.domain.Account;
import jkeypass.service.DatabaseService;
import jkeypass.ui.AccountsTableModel.Column;
import jkeypass.ui.util.Dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/**
 * Accounts grid: double click on Login/Password copies the value to the
 * clipboard, on URL opens it in the browser.
 */
public final class AccountsTable extends JTable {
	private final DatabaseService database;
	private final Component dialogParent;

	public AccountsTable(DatabaseService database, AccountsTableModel model, Component dialogParent) {
		super(model);

		this.database = database;
		this.dialogParent = dialogParent;

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setComponentPopupMenu(new JPopupMenu());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				if (row != -1) {
					getSelectionModel().setSelectionInterval(row, row);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					onCellDoubleClick(e);
				}
			}
		});
	}

	public void setPopupActions(Action create, Action edit, Action remove) {
		JPopupMenu menu = getComponentPopupMenu();
		menu.add(create);
		menu.add(edit);
		menu.add(remove);
	}

	private void onCellDoubleClick(MouseEvent e) {
		int row = rowAtPoint(e.getPoint());
		int column = columnAtPoint(e.getPoint());
		if (row == -1 || column == -1 || !database.isOpen()) {
			return;
		}

		Account account = database.get(row);
		Column columnType = Column.values()[column];

		switch (columnType) {
			case LOGIN -> copyToClipboard(account.login());
			case PASSWORD -> copyToClipboard(account.password());
			case URL -> openUrl(account.url());
			default -> {
			}
		}
	}

	private void copyToClipboard(String value) {
		if (value.isEmpty()) {
			return;
		}
		try {
			StringSelection selection = new StringSelection(value);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		} catch (Exception e) {
			Dialogs.warning(dialogParent, "Буфер обмена недоступен");
		}
	}

	private void openUrl(String url) {
		if (url.isEmpty()) {
			return;
		}
		try {
			if (Desktop.isDesktopSupported()
					&& Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (Exception e) {
			Dialogs.warning(dialogParent, "Неправильный URL");
		}
	}
}
