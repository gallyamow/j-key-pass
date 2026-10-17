package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class ImportAction extends AbstractAction {
	private final AppController controller;

	public ImportAction(AppController controller) {
		super("Импортировать", Resources.icon("import.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.importAccounts();
	}
}
