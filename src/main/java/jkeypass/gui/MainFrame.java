package jkeypass.gui;

import jkeypass.models.Account;
import jkeypass.models.AccountsDatabase;
import jkeypass.models.AccountsTableModel;
import jkeypass.tools.Config;
import jkeypass.tools.Resources;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
	private AccountsDatabase database;

	private JTable grid;

	public MainFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(640, 480);

		JMenuBar menuBar = this.createMenuBar();
		setJMenuBar(menuBar);

		JToolBar toolBar = this.createToolBar();
		this.add((new JPanel(new FlowLayout(FlowLayout.LEFT))).add(toolBar), BorderLayout.NORTH);

		this.grid = this.createGrid();
		this.add(new JScrollPane(this.grid));
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
			this.showErrorMessage("Не удалось открыть файл");
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

				int rowNumber = grid.rowAtPoint(e.getPoint());

				ListSelectionModel model = grid.getSelectionModel();

				model.setSelectionInterval(rowNumber, rowNumber);
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(new CreateAccountAction("Добавить новую запись"));
		menu.add(new EditAccountAction("Редактировать выбранную запись"));
		menu.add(new RemoveAccountAction("Удалить выбранную запись"));

		grid.setComponentPopupMenu(menu);

		return grid;
	}

	private JMenuBar createMenuBar() {
		JMenu fileMenu = new JMenu("Файл");

		fileMenu.add(new CreateFileAction("Создать базу паролей"));
		fileMenu.add(new OpenFileAction("Открыть базу паролей"));
		fileMenu.add(new SaveFileAction("Сохранить базу паролей"));
		fileMenu.addSeparator();

		fileMenu.add(new JMenuItem("Изменить основной пароль"));
		fileMenu.addSeparator();

		fileMenu.add(new ExitAction("Выйти из программы"));

		JMenu accountsMenu = new JMenu("Записи");
		accountsMenu.add(new CreateAccountAction("Добавить новую запись"));
		accountsMenu.add(new EditAccountAction("Редактировать выбранную запись"));
		accountsMenu.add(new RemoveAccountAction("Удалить выбранную запись"));
		accountsMenu.addSeparator();

		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(accountsMenu);

		return bar;
	}

	private JToolBar createToolBar() {
		Resources resources = new Resources();

		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		bar.add(new CreateFileAction("Создать базу паролей", resources.getIcon("create-file.png")));
		bar.add(new OpenFileAction("Открыть базу паролей", resources.getIcon("open-file.png")));
		bar.add(new SaveFileAction("Сохранить базу паролей", resources.getIcon("save-file.png")));

		bar.addSeparator();

		bar.add(new CreateAccountAction("Добавить новую запись", resources.getIcon("create-account.png")));
		bar.add(new EditAccountAction("Редактировать выбранную запись", resources.getIcon("edit-account.png")));
		bar.add(new RemoveAccountAction("Удалить выбранную запись", resources.getIcon("remove-account.png")));

		return bar;
	}

	private void openDatabase(File file) {
		this.loadDatabase(file);

		this.setTitle(this.getTitle());
	}

	private void saveDatabase() {
		if (this.database == null) {
			showErrorMessage("Файл базы паролей не открыт");
		}

		try {
			this.database.save();
		} catch (IOException e1) {
			showErrorMessage("Не удалось записать данные в файл");
		}
	}

	private void refreshGrid() {
		((AbstractTableModel) grid.getModel()).fireTableDataChanged();
	}

	private void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

	private class CreateFileAction extends AbstractAction {
		private CreateFileAction(String name) {
			super(name);
		}

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
					showErrorMessage("Не удалось сохранить файл");
				}
			}
		}

		private void createNewFile(File selectedFile) throws IOException {
			File file = selectedFile;

			if (!selectedFile.getAbsolutePath().endsWith(Config.baseExtension)) {
				file = new File(selectedFile + "." + Config.baseExtension);
			}

			file.createNewFile();

			openDatabase(file);
		}
	}

	private class OpenFileAction extends AbstractAction {
		private OpenFileAction(String name) {
			super(name);
		}

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
		public SaveFileAction(String name) {
			super(name);
		}

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

			// todo: если будет сортировка - наверно не будет совпадать
			int index = selectedRow;

			AccountDialog accountFrame = new AccountDialog(database.get(index), MainFrame.this, "Новая запись");

			if (accountFrame.showDialog() == AccountDialog.SAVE_OPTION) {
				database.update(index, accountFrame.getAccount());

				saveDatabase();

				refreshGrid();
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

			int index = selectedRow;
			database.remove(index);

			refreshGrid();
		}
	}

	private class ExitAction extends AbstractAction {
		public ExitAction(String name) {
			super(name);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (database != null) {
				try {
					database.close();
				} catch (IOException e1) {
					showErrorMessage("Не удалось закрыть файл");
				}
			}
			System.exit(0);
		}
	}
}
