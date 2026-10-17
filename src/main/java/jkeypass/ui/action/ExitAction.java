package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class ExitAction extends AbstractAction {
	private final AppController controller;

	public ExitAction(AppController controller) {
		super("Закрыть программу", Resources.icon("exit.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.exit();
	}
}
