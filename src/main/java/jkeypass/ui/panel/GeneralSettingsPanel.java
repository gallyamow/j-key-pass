package jkeypass.ui.panel;

import jkeypass.settings.AppSettings;
import jkeypass.sync.SyncMethod;
import jkeypass.ui.dialog.SyncSettingsDialog;
import jkeypass.ui.util.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;

public final class GeneralSettingsPanel extends JPanel {
	private final AppSettings settings;

	private final JComboBox<UIManager.LookAndFeelInfo> themeBox = new JComboBox<>();
	private final JComboBox<SyncMethod> syncMethodBox = new JComboBox<>(SyncMethod.values());

	public GeneralSettingsPanel(AppSettings settings) {
		super(new GridBagLayout());

		this.settings = settings;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		addThemeRow(0);
		addSyncRow(1);
	}

	public void applyTo(AppSettings settings) {
		UIManager.LookAndFeelInfo theme = (UIManager.LookAndFeelInfo) themeBox.getSelectedItem();
		if (theme != null) {
			settings.setTheme(theme.getClassName());
		}

		SyncMethod method = (SyncMethod) syncMethodBox.getSelectedItem();
		if (method != null) {
			settings.setSyncMethod(method);
		}
	}

	private void addThemeRow(int row) {
		add(new JLabel("Тема:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, row));

		themeBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value,
														  int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof UIManager.LookAndFeelInfo info) {
					setText(info.getName());
				}
				return this;
			}
		});

		for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			themeBox.addItem(info);
			if (info.getClassName().equals(settings.theme())) {
				themeBox.setSelectedItem(info);
			}
		}

		add(themeBox, GridBagLayoutHelper.gbc(1, row));
	}

	private void addSyncRow(int row) {
		add(new JLabel("Синхронизация:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, row));

		syncMethodBox.setSelectedItem(settings.syncMethod());

		JButton configureButton = new JButton("Настройка синхронизации");
		configureButton.addActionListener(e -> {
			SyncMethod method = (SyncMethod) syncMethodBox.getSelectedItem();
			Window parent = SwingUtilities.windowForComponent(configureButton);

			new SyncSettingsDialog(parent, method).showDialog();
		});

		syncMethodBox.addActionListener(e ->
				configureButton.setEnabled(syncMethodBox.getSelectedItem() != SyncMethod.NONE));

		configureButton.setEnabled(settings.syncMethod() != SyncMethod.NONE);

		JPanel container = new JPanel();
		container.add(syncMethodBox);
		container.add(configureButton);

		add(container, GridBagLayoutHelper.gbc(1, row));
	}
}
