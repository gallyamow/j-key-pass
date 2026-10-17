package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class EditAccountAction extends AbstractAction {
	private final AppController controller;

	public EditAccountAction(AppController controller) {
		super("Редактировать выбранную запись", Resources.icon("edit-account.png"));
		this.controller = controller;
	}

	public EditAccountAction(AppController controller, String name) {
		super(name);
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.editAccount();
	}
}
