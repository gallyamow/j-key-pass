package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class SaveFileAction extends AbstractAction {
	private final AppController controller;

	public SaveFileAction(AppController controller) {
		super("Сохранить базу паролей", Resources.icon("save-file.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.saveDatabase();
	}
}
