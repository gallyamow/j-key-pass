package jkeypass.gui;

import jkeypass.models.Account;
import jkeypass.models.AccountsDatabase;
import jkeypass.models.AccountsTableModel;
import jkeypass.models.Settings;
import jkeypass.tools.Config;
import jkeypass.tools.Resources;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
	enum Action {
		CREATE_FILE_ACTION,
		OPEN_FILE_ACTION,
		SAVE_FILE_ACTION,

		CREATE_ACCOUNT_ACTION,
		EDIT_ACCOUNT_ACTION,
		REMOVE_ACCOUNT_ACTION,

		SETTINGS_ACTION,
		EXIT_ACTION
	}

	private AccountsDatabase database;

	private JTable grid;

	private Map<Action, AbstractAction> actions = new HashMap<>();

	public MainFrame() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				close();
			}
		});

		this.setSize(640, 480);

		this.applySettings();

		this.grid = this.createGrid();
		this.add(new JScrollPane(this.grid));

		this.initActions();

		JMenuBar menuBar = this.createMenu();
		setJMenuBar(menuBar);

		JToolBar toolBar = this.createToolBar();
		this.add((new JPanel(new FlowLayout(FlowLayout.LEFT))).add(toolBar), BorderLayout.NORTH);
	}

	public void loadDatabase(File databaseFile) {
		this.database = new AccountsDatabase(databaseFile);

		if (this.database.isLocked()) {
			int dialog = JOptionPane.showConfirmDialog(this, "Файл либо уже открыт, либо программа была некорректно завершена. " +
					"Все равно открыть?", "Warning", JOptionPane.YES_NO_OPTION);

			if (dialog == JOptionPane.NO_OPTION) {
				return;
			}
		}

		try {
			this.database.open();
		} catch (Exception e) {
			e.printStackTrace();
			this.showError("Не удалось открыть файл");
		}

		this.grid.setModel(new AccountsTableModel(this.database));
	}

	@Override
	public String getTitle() {
		String title = "j-key-pass";

		if (this.database != null) {
			title += " - " + this.database.getFile().getPath();
		}

		return title;
	}

	private JTable createGrid() {
		final JTable grid = new JTable();

		grid.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);

				int rowIndex = this.getRowIndex(e);

				if (rowIndex == -1) {
					return;
				}

				grid.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);

				refreshEnabledActions();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (e.getClickCount() == 2) {
					int rowIndex = this.getRowIndex(e);
					if (rowIndex == -1) {
						return;
					}

					int columnIndex = this.getColumnIndex(e);
					if (columnIndex == -1) {
						return;
					}

					int index = ((AccountsTableModel) grid.getModel()).getDatabaseIndexByRowIndex(rowIndex);

					Account account = database.get(index);

					AccountsTableModel.Column column = AccountsTableModel.Column.values()[columnIndex];

					String value = null;

					if (column == AccountsTableModel.Column.LOGIN) {
						value = account.getLogin();
					} else if (column == AccountsTableModel.Column.PASSWORD) {
						value = account.getPassword();
					} else if (column == AccountsTableModel.Column.URL) {
						value = account.getUrl();
					}

					if (value != null) {
						if (column == AccountsTableModel.Column.URL) {
							if (java.awt.Desktop.isDesktopSupported()) {
								java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

								if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
									try {
										java.net.URI uri = new java.net.URI(value);
										desktop.browse(uri);
									} catch (Exception ex) {
										showWarning("Неправильный URL");
									}
								}
							}
						} else {
							try {
								StringSelection selection = new StringSelection(value);
								Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
								clipboard.setContents(selection, selection);
							} catch (Exception ex) {
								showWarning("Буфер обмена недоступен");
							}
						}
					}
				}
			}

			private int getRowIndex(MouseEvent e) {
				return grid.rowAtPoint(e.getPoint());
			}

			private int getColumnIndex(MouseEvent e) {
				return grid.columnAtPoint(e.getPoint());
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(new CreateAccountAction("Добавить новую запись"));
		menu.add(new EditAccountAction("Редактировать выбранную запись"));
		menu.add(new RemoveAccountAction("Удалить выбранную запись"));

		grid.setComponentPopupMenu(menu);

		return grid;
	}

	private JMenuBar createMenu() {
		JMenu fileMenu = new JMenu("Файл");

		JMenuItem item;

		item = new JMenuItem(this.actions.get(Action.CREATE_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		fileMenu.add(item);

		item = new JMenuItem(this.actions.get(Action.OPEN_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		fileMenu.add(item);

		item = new JMenuItem(this.actions.get(Action.SAVE_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		fileMenu.add(item);

		fileMenu.addSeparator();

		fileMenu.add(this.actions.get(Action.SETTINGS_ACTION));

		fileMenu.addSeparator();

		item = new JMenuItem(this.actions.get(Action.EXIT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
		fileMenu.add(item);

		JMenu accountsMenu = new JMenu("Записи");

		item = new JMenuItem(this.actions.get(Action.CREATE_ACCOUNT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("alt INSERT"));
		accountsMenu.add(item);

		accountsMenu.add(this.actions.get(Action.EDIT_ACCOUNT_ACTION));
		accountsMenu.add(this.actions.get(Action.REMOVE_ACCOUNT_ACTION));
		accountsMenu.addSeparator();

		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(accountsMenu);

		return bar;
	}

	private JToolBar createToolBar() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		bar.add(this.actions.get(Action.CREATE_FILE_ACTION));
		bar.add(this.actions.get(Action.OPEN_FILE_ACTION));
		bar.add(this.actions.get(Action.SAVE_FILE_ACTION));

		bar.addSeparator();

		bar.add(this.actions.get(Action.CREATE_ACCOUNT_ACTION));
		bar.add(this.actions.get(Action.EDIT_ACCOUNT_ACTION));
		bar.add(this.actions.get(Action.REMOVE_ACCOUNT_ACTION));

		return bar;
	}

	private void initActions() {
		Resources resources = new Resources();

		this.actions.put(Action.CREATE_FILE_ACTION, new CreateFileAction("Создать базу паролей",
				resources.getIcon("create-file.png")));
		this.actions.put(Action.OPEN_FILE_ACTION, new OpenFileAction("Открыть базу паролей",
				resources.getIcon("open-file.png")));
		this.actions.put(Action.SAVE_FILE_ACTION, new SaveFileAction("Сохранить базу паролей",
				resources.getIcon("save-file.png")));

		this.actions.put(Action.CREATE_ACCOUNT_ACTION, new CreateAccountAction("Добавить новую запись",
				resources.getIcon("create-account.png")));
		this.actions.put(Action.EDIT_ACCOUNT_ACTION, new EditAccountAction("Редактировать выбранную запись",
				resources.getIcon("edit-account.png")));
		this.actions.put(Action.REMOVE_ACCOUNT_ACTION, new RemoveAccountAction("Удалить выбранную запись",
				resources.getIcon("remove-account.png")));

		this.actions.put(Action.SETTINGS_ACTION, new SettingsAction("Настройки", resources.getIcon("settings.png")));
		this.actions.put(Action.EXIT_ACTION, new ExitAction("Закрыть программу", resources.getIcon("exit.png")));

		this.refreshEnabledActions();
	}

	private void refreshEnabledActions() {
		boolean fileOpened = this.database != null && this.database.getFile() != null;
		boolean rowSelected = this.grid.getSelectedRow() != -1;

		if (fileOpened) {
			this.actions.get(Action.SAVE_FILE_ACTION).setEnabled(true);
			this.actions.get(Action.CREATE_ACCOUNT_ACTION).setEnabled(true);
		} else {
			this.actions.get(Action.SAVE_FILE_ACTION).setEnabled(false);
			this.actions.get(Action.CREATE_ACCOUNT_ACTION).setEnabled(false);
		}

		if (fileOpened && rowSelected) {
			this.actions.get(Action.EDIT_ACCOUNT_ACTION).setEnabled(true);
			this.actions.get(Action.REMOVE_ACCOUNT_ACTION).setEnabled(true);
		} else {
			this.actions.get(Action.EDIT_ACCOUNT_ACTION).setEnabled(false);
			this.actions.get(Action.REMOVE_ACCOUNT_ACTION).setEnabled(false);
		}
	}

	private void openDatabase(File file) {
		this.loadDatabase(file);

		this.setTitle(this.getTitle());

		this.refreshEnabledActions();
	}

	private void saveDatabase() {
		if (this.database == null) {
			showError("Файл базы паролей не открыт");
		}

		try {
			this.database.save();
		} catch (IOException e1) {
			showError("Не удалось записать данные в файл");
		}
	}

	private void close() {
		if (database != null) {
			try {
				database.close();
			} catch (IOException e) {
				showError("Не удалось закрыть файл");
			}
		}
	}

	private void refreshGrid() {
		((AbstractTableModel) grid.getModel()).fireTableDataChanged();
	}

	private void applySettings() {
		Settings settings = new Settings();

		try {
			UIManager.setLookAndFeel(settings.getTheme());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
		System.exit(0);
	}

	private class CreateFileAction extends AbstractAction {
		private CreateFileAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

			chooser.setFileFilter(new FileNameExtensionFilter("Файлы баз паролей", Config.baseExtension));

			int result = chooser.showSaveDialog(MainFrame.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				try {
					this.createNewFile(chooser.getSelectedFile());
				} catch (IOException ex) {
					showError("Не удалось сохранить файл");
				}
			}
		}

		private void createNewFile(File selectedFile) throws IOException {
			File file = selectedFile;

			if (!selectedFile.getAbsolutePath().endsWith(Config.baseExtension)) {
				file = new File(selectedFile + "." + Config.baseExtension);
			}

			if (file.createNewFile()) {
				openDatabase(file);
			}
		}
	}

	private class OpenFileAction extends AbstractAction {
		private OpenFileAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();

			chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

			chooser.setFileFilter(new FileNameExtensionFilter("Файлы баз паролей", Config.baseExtension));

			int result = chooser.showOpenDialog(MainFrame.this);

			if (result == JFileChooser.APPROVE_OPTION) {
				openDatabase(chooser.getSelectedFile());
			}
		}
	}

	private class SaveFileAction extends AbstractAction {
		public SaveFileAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			saveDatabase();
		}
	}

	private class CreateAccountAction extends AbstractAction {
		public CreateAccountAction(String name) {
			super(name);
		}

		public CreateAccountAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			AccountDialog accountFrame = new AccountDialog(new Account(), MainFrame.this, "Новая запись");

			if (accountFrame.showDialog() == AccountDialog.SAVE_OPTION) {
				database.add(accountFrame.getAccount());

				saveDatabase();

				refreshGrid();
				refreshEnabledActions();
			}
		}
	}

	private class EditAccountAction extends AbstractAction {
		public EditAccountAction(String name) {
			super(name);
		}

		public EditAccountAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = grid.getSelectedRow();

			if (selectedRow == -1) {
				return;
			}

			int index = ((AccountsTableModel) grid.getModel()).getDatabaseIndexByRowIndex(selectedRow);

			AccountDialog accountFrame = new AccountDialog(database.get(index), MainFrame.this, "Редактирование записи");

			if (accountFrame.showDialog() == AccountDialog.SAVE_OPTION) {
				database.update(index, accountFrame.getAccount());

				saveDatabase();

				refreshGrid();
				refreshEnabledActions();
			}
		}
	}

	private class RemoveAccountAction extends AbstractAction {
		public RemoveAccountAction(String name) {
			super(name);
		}

		public RemoveAccountAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = grid.getSelectedRow();

			if (selectedRow == -1) {
				return;
			}

			int index = ((AccountsTableModel) grid.getModel()).getDatabaseIndexByRowIndex(selectedRow);

			database.remove(index);

			refreshGrid();
			refreshEnabledActions();
		}
	}

	private class SettingsAction extends AbstractAction {
		public SettingsAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SettingsDialog settingsDialog = new SettingsDialog(MainFrame.this, "Настройки");

			if (settingsDialog.showDialog() == SettingsDialog.SAVE_OPTION) {
				applySettings();
			}
		}
	}

	private class ExitAction extends AbstractAction {
		public ExitAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			close();

			System.exit(0);
		}
	}
}
