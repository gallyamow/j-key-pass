package jkeypass.ui.action;

import jkeypass.ui.AppController;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.event.ActionEvent;

public final class SettingsAction extends AbstractAction {
	private final AppController controller;

	public SettingsAction(AppController controller) {
		super("Настройки", Resources.icon("settings.png"));
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.editSettings();
	}
}
