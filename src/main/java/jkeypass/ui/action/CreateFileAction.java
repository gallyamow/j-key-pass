package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class CreateFileAction extends AbstractAction {
	private final AppController controller;

	public CreateFileAction(AppController controller) {
		super("Создать базу паролей", Resources.icon("create-file.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.createDatabase();
	}
}
