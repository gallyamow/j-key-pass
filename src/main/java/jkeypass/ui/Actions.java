package jkeypass.ui;

import jkeypass.ui.action.CreateAccountAction;
import jkeypass.ui.action.CreateFileAction;
import jkeypass.ui.action.EditAccountAction;
import jkeypass.ui.action.ExitAction;
import jkeypass.ui.action.ImportAction;
import jkeypass.ui.action.OpenFileAction;
import jkeypass.ui.action.RemoveAccountAction;
import jkeypass.ui.action.SaveFileAction;
import jkeypass.ui.action.SettingsAction;

import javax.swing.*;

public final class Actions {
	public final Action createFile;
	public final Action openFile;
	public final Action saveFile;
	public final Action createAccount;
	public final Action editAccount;
	public final Action removeAccount;
	public final Action importAccounts;
	public final Action settings;
	public final Action exit;

	public Actions(AppController controller) {
		createFile = new CreateFileAction(controller);
		openFile = new OpenFileAction(controller);
		saveFile = new SaveFileAction(controller);
		createAccount = new CreateAccountAction(controller);
		editAccount = new EditAccountAction(controller);
		removeAccount = new RemoveAccountAction(controller);
		importAccounts = new ImportAction(controller);
		settings = new SettingsAction(controller);
		exit = new ExitAction(controller);
	}
}
