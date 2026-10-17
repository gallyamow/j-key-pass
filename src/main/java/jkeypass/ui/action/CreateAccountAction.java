package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class CreateAccountAction extends AbstractAction {
	private final AppController controller;

	public CreateAccountAction(AppController controller) {
		super("Добавить новую запись", Resources.icon("create-account.png"));
		this.controller = controller;
	}

	public CreateAccountAction(AppController controller, String name) {
		super(name);
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.createAccount();
	}
}
