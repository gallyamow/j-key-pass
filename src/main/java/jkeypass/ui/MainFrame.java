package jkeypass.ui;

import jkeypass.Application;
import jkeypass.ui.tray.TraySupport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainFrame extends JFrame {
	private final AppController controller;
	private final Actions actions;
	private final AccountsTable table;
	private final AccountsTableModel tableModel;
	private final StatusBar statusBar = new StatusBar();

	public MainFrame() {
		controller = new AppController(this);
		controller.init();

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setSize(640, 480);
		setTitle(Application.APP_NAME);

		actions = new Actions(controller);

		tableModel = new AccountsTableModel(controller.database());
		table = new AccountsTable(controller.database(), tableModel, this);
		table.setPopupActions(
				new jkeypass.ui.action.CreateAccountAction(controller, "Добавить новую запись"),
				new jkeypass.ui.action.EditAccountAction(controller, "Редактировать выбранную запись"),
				new jkeypass.ui.action.RemoveAccountAction(controller, "Удалить выбранную запись"));
		table.getSelectionModel().addListSelectionListener(e -> refreshActionsState());
		add(new JScrollPane(table));

		setJMenuBar(menuBar());

		JToolBar toolBar = toolBar();
		add(new JPanel(new FlowLayout(FlowLayout.LEFT)).add(toolBar), BorderLayout.NORTH);

		add(statusBar, BorderLayout.SOUTH);

		TraySupport.install(this, actions);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				setVisible(false);
			}
		});

		refreshActionsState();
	}

	public StatusBar statusBar() {
		return statusBar;
	}

	public int selectedRow() {
		return table.getSelectedRow();
	}

	public void onDatabaseChanged() {
		tableModel.fireTableDataChanged();
		updateTitle();
		refreshActionsState();
	}

	public void restoreWindow() {
		setVisible(true);
		setExtendedState(getExtendedState() & ~JFrame.ICONIFIED);
		requestFocus();
	}

	private void updateTitle() {
		String title = Application.APP_NAME;
		if (controller.database().isOpen()) {
			title += " - " + controller.database().file();
		}
		setTitle(title);
	}

	private void refreshActionsState() {
		boolean open = controller.database().isOpen();
		boolean rowSelected = table.getSelectedRow() != -1;

		actions.saveFile.setEnabled(open);
		actions.importAccounts.setEnabled(open);
		actions.createAccount.setEnabled(open);
		actions.editAccount.setEnabled(open && rowSelected);
		actions.removeAccount.setEnabled(open && rowSelected);
	}

	private JMenuBar menuBar() {
		JMenu fileMenu = new JMenu("Файл");
		fileMenu.add(menuItem(actions.createFile, "ctrl N"));
		fileMenu.add(menuItem(actions.openFile, "ctrl O"));
		fileMenu.add(menuItem(actions.saveFile, "ctrl S"));
		fileMenu.addSeparator();
		fileMenu.add(actions.importAccounts);
		fileMenu.addSeparator();
		fileMenu.add(actions.settings);
		fileMenu.addSeparator();
		fileMenu.add(menuItem(actions.exit, "ctrl Q"));

		JMenu accountsMenu = new JMenu("Записи");
		accountsMenu.add(menuItem(actions.createAccount, "alt INSERT"));
		accountsMenu.add(menuItem(actions.editAccount, "ctrl E"));
		accountsMenu.add(menuItem(actions.removeAccount, "ctrl D"));

		JMenuBar bar = new JMenuBar();
		bar.add(fileMenu);
		bar.add(accountsMenu);

		return bar;
	}

	private JMenuItem menuItem(Action action, String accelerator) {
		JMenuItem item = new JMenuItem(action);
		item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
		return item;
	}

	private JToolBar toolBar() {
		JToolBar bar = new JToolBar();
		bar.setFloatable(false);

		bar.add(actions.createFile);
		bar.add(actions.openFile);
		bar.add(actions.saveFile);
		bar.addSeparator();
		bar.add(actions.createAccount);
		bar.add(actions.editAccount);
		bar.add(actions.removeAccount);

		return bar;
	}
}
