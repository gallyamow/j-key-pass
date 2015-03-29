package jkeypass.gui;

import jkeypass.common.Config;
import jkeypass.common.Crypto;
import jkeypass.common.Resources;
import jkeypass.models.Account;
import jkeypass.models.AccountsDatabase;
import jkeypass.models.AccountsTableModel;
import jkeypass.models.Settings;
import jkeypass.sync.Sync;
import jkeypass.sync.SyncException;
import jkeypass.sync.Synchronizer;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
	private final String APP_NAME = "j-key-pass";

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

	private StatusBar statusBar;

	private Map<Action, AbstractAction> actions = new HashMap<>();

	private Settings settings;
	private Synchronizer synchronizer;

	private Crypto crypto;

	public MainFrame() {
		settings = new Settings();
		synchronizer = Sync.getSynchronizer(settings.getSyncMethod());

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				close();
			}
		});

		setSize(640, 480);

		applySettings();

		grid = grid();
		add(new JScrollPane(grid));

		initActions();

		JMenuBar menuBar = menuBar();
		setJMenuBar(menuBar);

		JToolBar toolBar = toolBar();
		add((new JPanel(new FlowLayout(FlowLayout.LEFT))).add(toolBar), BorderLayout.NORTH);

		statusBar = new StatusBar(this);
		add(statusBar, BorderLayout.SOUTH);

		setTrayIcon();
	}

	private void setTrayIcon() {
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		Resources resources = new Resources();

		TrayIcon icon = new TrayIcon(resources.getImage("tray.png"), APP_NAME);
		icon.setImageAutoSize(true);

		icon.setPopupMenu(trayMenu());

		SystemTray tray = SystemTray.getSystemTray();

		try {
			tray.add(icon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}
	}

	public void loadDatabase(File databaseFile) {
		database = new AccountsDatabase(databaseFile, crypto);

		if (database.isLocked()) {
			int dialog = JOptionPane.showConfirmDialog(this, "Файл либо уже открыт, либо программа была некорректно завершена. " +
					"Все равно открыть?", "Warning", JOptionPane.YES_NO_OPTION);

			if (dialog == JOptionPane.NO_OPTION) {
				return;
			}
		}

		try {
			database.open();
		} catch (StreamCorruptedException e) {
			showError("Неправильный пароль");
		} catch (Exception e) {
			showError("Не удалось открыть файл");
		}

		if (database.isOpen()) {
			grid.setModel(new AccountsTableModel(database));
		}
	}

	@Override
	public String getTitle() {
		String title = APP_NAME;

		if (database != null) {
			title += " - " + database.getFile().getPath();
		}

		return title;
	}

	private JTable grid() {
		final JTable grid = new JTable();

		grid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		grid.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);

				int rowIndex = getRowIndex(e);

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
					int rowIndex = getRowIndex(e);
					if (rowIndex == -1) {
						return;
					}

					int columnIndex = getColumnIndex(e);
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

	private PopupMenu trayMenu() {
		class MenuActionListener implements ActionListener {
			private AbstractAction action;
			
			public MenuActionListener(AbstractAction action) {
				this.action = action;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}
		}

		PopupMenu popup = new PopupMenu();
		MenuItem item;
		
		AbstractAction action;

		action = actions.get(Action.CREATE_FILE_ACTION);
		item = new MenuItem((String) action.getValue(javax.swing.Action.NAME));
		item.addActionListener(new MenuActionListener(action));
		popup.add(item);

		action = actions.get(Action.OPEN_FILE_ACTION);
		item = new MenuItem((String) action.getValue(javax.swing.Action.NAME));
		item.addActionListener(new MenuActionListener(action));
		popup.add(item);

		action = actions.get(Action.SAVE_FILE_ACTION);
		item = new MenuItem((String) action.getValue(javax.swing.Action.NAME));
		item.addActionListener(new MenuActionListener(action));
		popup.add(item);

		action = actions.get(Action.SETTINGS_ACTION);
		item = new MenuItem((String) action.getValue(javax.swing.Action.NAME));
		item.addActionListener(new MenuActionListener(action));
		popup.add(item);

		popup.addSeparator();

		action = actions.get(Action.EXIT_ACTION);
		item = new MenuItem((String) action.getValue(javax.swing.Action.NAME));
		item.addActionListener(new MenuActionListener(action));
		popup.add(item);

		return popup;
	}

	private JMenuBar menuBar() {
		JMenu fileMenu = new JMenu("Файл");

		JMenuItem item;

		item = new JMenuItem(actions.get(Action.CREATE_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
		fileMenu.add(item);

		item = new JMenuItem(actions.get(Action.OPEN_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
		fileMenu.add(item);

		item = new JMenuItem(actions.get(Action.SAVE_FILE_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
		fileMenu.add(item);

		fileMenu.addSeparator();

		fileMenu.add(actions.get(Action.SETTINGS_ACTION));

		fileMenu.addSeparator();

		item = new JMenuItem(actions.get(Action.EXIT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
		fileMenu.add(item);

		JMenu accountsMenu = new JMenu("Записи");

		item = new JMenuItem(actions.get(Action.CREATE_ACCOUNT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("alt INSERT"));
		accountsMenu.add(item);

		item = new JMenuItem(actions.get(Action.EDIT_ACCOUNT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
		accountsMenu.add(item);

		item = new JMenuItem(actions.get(Action.REMOVE_ACCOUNT_ACTION));
		item.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
		accountsMenu.add(item);

		accountsMenu.addSeparator();

		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(accountsMenu);

		return bar;
	}

	private JToolBar toolBar() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		bar.add(actions.get(Action.CREATE_FILE_ACTION));
		bar.add(actions.get(Action.OPEN_FILE_ACTION));
		bar.add(actions.get(Action.SAVE_FILE_ACTION));

		bar.addSeparator();

		bar.add(actions.get(Action.CREATE_ACCOUNT_ACTION));
		bar.add(actions.get(Action.EDIT_ACCOUNT_ACTION));
		bar.add(actions.get(Action.REMOVE_ACCOUNT_ACTION));

		return bar;
	}

	private void initActions() {
		Resources resources = new Resources();

		actions.put(Action.CREATE_FILE_ACTION, new CreateFileAction("Создать базу паролей",
				resources.getIcon("create-file.png")));
		actions.put(Action.OPEN_FILE_ACTION, new OpenFileAction("Открыть базу паролей",
				resources.getIcon("open-file.png")));
		actions.put(Action.SAVE_FILE_ACTION, new SaveFileAction("Сохранить базу паролей",
				resources.getIcon("save-file.png")));

		actions.put(Action.CREATE_ACCOUNT_ACTION, new CreateAccountAction("Добавить новую запись",
				resources.getIcon("create-account.png")));
		actions.put(Action.EDIT_ACCOUNT_ACTION, new EditAccountAction("Редактировать выбранную запись",
				resources.getIcon("edit-account.png")));
		actions.put(Action.REMOVE_ACCOUNT_ACTION, new RemoveAccountAction("Удалить выбранную запись",
				resources.getIcon("remove-account.png")));

		actions.put(Action.SETTINGS_ACTION, new SettingsAction("Настройки", resources.getIcon("settings.png")));
		actions.put(Action.EXIT_ACTION, new ExitAction("Закрыть программу", resources.getIcon("exit.png")));

		refreshEnabledActions();
	}

	private void refreshEnabledActions() {
		boolean fileOpened = database != null && database.getFile() != null;
		boolean rowSelected = grid.getSelectedRow() != -1;

		if (fileOpened) {
			actions.get(Action.SAVE_FILE_ACTION).setEnabled(true);
			actions.get(Action.CREATE_ACCOUNT_ACTION).setEnabled(true);
		} else {
			actions.get(Action.SAVE_FILE_ACTION).setEnabled(false);
			actions.get(Action.CREATE_ACCOUNT_ACTION).setEnabled(false);
		}

		if (fileOpened && rowSelected) {
			actions.get(Action.EDIT_ACCOUNT_ACTION).setEnabled(true);
			actions.get(Action.REMOVE_ACCOUNT_ACTION).setEnabled(true);
		} else {
			actions.get(Action.EDIT_ACCOUNT_ACTION).setEnabled(false);
			actions.get(Action.REMOVE_ACCOUNT_ACTION).setEnabled(false);
		}
	}

	private void openDatabase(File file) {
		if (synchronizer != null) {
			File syncFile = null;

			statusBar.setText("Загрузка из хранилища...");

			try {
				syncFile = synchronizer.load(file);
			} catch (SyncException e) {
				showWarning(e.getMessage());
			}

			if (syncFile != null) {
				try {
					if (!FileUtils.contentEquals(file, syncFile)) {
						int question = JOptionPane.showConfirmDialog(this, "Файл из хранилища и локальный файл не совпадают." +
								"Использовать файл из хранилища?", "Warning", JOptionPane.YES_NO_OPTION);

						if (question == JOptionPane.YES_OPTION) {
							syncFile.renameTo(file);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		statusBar.setText("Чтение файла...");

		loadDatabase(file);

		setTitle(getTitle());

		refreshEnabledActions();

		statusBar.clear();
	}

	private void save() {
		statusBar.setText("Сохранение файла...");
		if (database != null) {
			statusBar.setText("Сохранение файла...");

			try {
				database.save();
			} catch (IOException e1) {
				showError("Не удалось записать данные в файл");
			} catch (SyncException e) {
				showWarning(e.getMessage());
			}

			if (synchronizer != null) {
				try {
					synchronizer.save(database.getFile());
				} catch (SyncException e) {
					showWarning(e.getMessage());
				}
			}

			statusBar.clear();
		}
	}

	private void close() {
		save();

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
		try {
			UIManager.setLookAndFeel(settings.getTheme());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	private boolean requestPassword() {
		PasswordDialog dialog = new PasswordDialog(MainFrame.this, "Введите главный пароль");
		if (dialog.showDialog() != PasswordDialog.OK_OPTION) {
			return false;
		}

		char[] password = dialog.getPassword();

		if (password.length == 0) {
			showError("Введен пустой пароль");

			return false;
		}

		crypto = new Crypto(password);

		return true;
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
					createNewFile(chooser.getSelectedFile());
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

			if (requestPassword()) {
				if (file.createNewFile()) {
					openDatabase(file);
				}
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
				if (requestPassword()) {
					openDatabase(chooser.getSelectedFile());
				}
			}
		}
	}

	private class SaveFileAction extends AbstractAction {
		public SaveFileAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			save();
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
			AccountDialog dialog = new AccountDialog(new Account(), MainFrame.this, "Новая запись");

			if (dialog.showDialog() == AccountDialog.SAVE_OPTION) {
				database.add(dialog.getAccount());

				save();

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

			AccountDialog dialog = new AccountDialog(database.get(index), MainFrame.this, "Редактирование записи");

			if (dialog.showDialog() == AccountDialog.SAVE_OPTION) {
				database.update(index, dialog.getAccount());

				save();

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
			SettingsDialog dialog = new SettingsDialog(MainFrame.this, "Настройки", settings);
			dialog.setLocationRelativeTo(MainFrame.this);

			if (dialog.showDialog() == SettingsDialog.SAVE_OPTION) {
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
