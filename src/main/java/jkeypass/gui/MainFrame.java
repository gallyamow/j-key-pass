package jkeypass.gui;

import jkeypass.models.Account;
import jkeypass.models.AccountsDatabase;
import jkeypass.models.AccountsTableModel;
import jkeypass.tools.Config;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
	private AccountsDatabase database;

	private JMenuBar menuBar;
	private JToolBar toolBar;
	private JTable grid;

	public MainFrame(String title) {
		super(title);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(640, 480);

		this.menuBar = this.createMenuBar();
		setJMenuBar(this.menuBar);

		this.toolBar = this.createToolBar();

		this.add((new JPanel(new FlowLayout(FlowLayout.LEFT))).add(this.toolBar), BorderLayout.NORTH);

		this.grid = this.createGrid();

		this.add(new JScrollPane(this.grid));
	}

	public void loadDatabase(File file) {
		this.database = new AccountsDatabase(file);

		if (this.database.isLocked()) {
			int dialog = JOptionPane.showConfirmDialog(this, "Файл либо уже открыт, либо программа была некорректно завершена. " +
					"Все равно открыть?", "Warning", JOptionPane.YES_NO_OPTION);

			if (dialog == JOptionPane.YES_NO_OPTION) {
				return;
			}
		}

		try {
			this.database.open();
		} catch (Exception e) {
			this.showErrorMessage("Не удалось открыть файл");
		}

		AccountsTableModel model = new AccountsTableModel(this.database);
		System.out.println(model.getRowCount());
		this.grid.setModel(new AccountsTableModel(this.database));
	}

	private JTable createGrid() {
		JTable grid = new JTable();
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
				file = new File(file + "." + Config.baseExtension);
			}

			file.createNewFile();
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
				loadDatabase(chooser.getSelectedFile());
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
			if (database != null) {
				try {
					database.save();
				} catch (IOException e1) {
					showErrorMessage("Не удалось записать данные в файл");
				}
			}
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
			// todo: remove
			for (int i = 0; i < 10; i++) {
				database.add(new Account("account " + i, "login " + i, "password " + i, "url " + i, "description " + i));
			}
			try {
				database.save();
			} catch (IOException e1) {
				e1.printStackTrace();
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
