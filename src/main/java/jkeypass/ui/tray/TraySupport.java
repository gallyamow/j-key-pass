package jkeypass.ui.tray;

import jkeypass.Application;
import jkeypass.ui.Actions;
import jkeypass.ui.MainFrame;
import jkeypass.ui.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TraySupport {
	private TraySupport() {
	}

	public static void install(MainFrame frame, Actions actions) {
		if (!SystemTray.isSupported()) {
			return;
		}

		TrayIcon icon = new TrayIcon(Resources.image("tray.png"), Application.APP_NAME, popupMenu(frame, actions));
		icon.setImageAutoSize(true);
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					frame.restoreWindow();
				}
			}
		});

		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			// tray is optional — the app works without it
		}
	}

	private static PopupMenu popupMenu(MainFrame frame, Actions actions) {
		PopupMenu popup = new PopupMenu();

		addItem(popup, frame, actions.createFile);
		addItem(popup, frame, actions.openFile);
		addItem(popup, frame, actions.saveFile);
		addItem(popup, frame, actions.settings);
		popup.addSeparator();
		addItem(popup, frame, actions.exit, false);

		return popup;
	}

	private static void addItem(PopupMenu popup, MainFrame frame, Action action) {
		addItem(popup, frame, action, true);
	}

	private static void addItem(PopupMenu popup, MainFrame frame, Action action, boolean restore) {
		MenuItem item = new MenuItem((String) action.getValue(Action.NAME));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (restore) {
					frame.restoreWindow();
				}
				action.actionPerformed(new ActionEvent(item, ActionEvent.ACTION_PERFORMED, null));
			}
		});
		popup.add(item);
	}
}
