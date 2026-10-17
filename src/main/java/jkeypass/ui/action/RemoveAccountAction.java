package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class RemoveAccountAction extends AbstractAction {
	private final AppController controller;

	public RemoveAccountAction(AppController controller) {
		super("Удалить выбранную запись", Resources.icon("remove-account.png"));
		this.controller = controller;
	}

	public RemoveAccountAction(AppController controller, String name) {
		super(name);
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.removeAccount();
	}
}
