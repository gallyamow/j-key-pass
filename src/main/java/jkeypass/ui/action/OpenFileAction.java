package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class OpenFileAction extends AbstractAction {
	private final AppController controller;

	public OpenFileAction(AppController controller) {
		super("Открыть базу паролей", Resources.icon("open-file.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.openDatabase();
	}
}
