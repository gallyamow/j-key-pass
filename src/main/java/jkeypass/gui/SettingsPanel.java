package jkeypass.gui;

import jkeypass.common.GridBagLayoutHelper;
import jkeypass.models.Settings;
import jkeypass.sync.Sync.Method;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JPanel implements jkeypass.common.SettingsPanel {
	private Settings settings;

	private JComboBox<UIManager.LookAndFeelInfo> themeBox;
	private JComboBox<Method> syncMethodBox;

	public SettingsPanel(Settings settings) {
		super();

		this.settings = settings;

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int i = 0;

		this.createThemeBox(++i);
		this.createSyncBox(++i);
	}

	@Override
	public Settings getUpdatedSettings() {
		String theme = ((UIManager.LookAndFeelInfo) this.themeBox.getSelectedItem()).getClassName();
		this.settings.setTheme(theme);

		String methodName = ((Method) this.syncMethodBox.getSelectedItem()).getName();
		this.settings.setSyncMethod(methodName);

		return this.settings;
	}

	private void createThemeBox(int rowIndex) {
		this.add(new JLabel(Settings.Options.THEME.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.createGbc(0, rowIndex));

		this.themeBox = new JComboBox<>();

		this.themeBox.setRenderer(new ListCellRenderer<UIManager.LookAndFeelInfo>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends UIManager.LookAndFeelInfo> list,
														  UIManager.LookAndFeelInfo value,
														  int index, boolean isSelected,
														  boolean cellHasFocus) {
				DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

				JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
						isSelected, cellHasFocus);

				renderer.setText(value.getName());

				return renderer;
			}
		});

		UIManager.LookAndFeelInfo selected = null;

		for (UIManager.LookAndFeelInfo item : UIManager.getInstalledLookAndFeels()) {
			themeBox.addItem(item);

			if (item.getClassName().equals(this.settings.getTheme())) {
				selected = item;
			}
		}

		this.themeBox.setSelectedItem(selected);

		this.add(this.themeBox, GridBagLayoutHelper.createGbc(1, rowIndex));
	}

	private void createSyncBox(int rowIndex) {
		Method selected = null;

		this.syncMethodBox = new JComboBox();
		for (Method item : Method.values()) {
			this.syncMethodBox.addItem(item);

			if (item.getName().equals(this.settings.getSyncMethod())) {
				selected = item;
			}
		}
		this.syncMethodBox.setSelectedItem(selected);

		final JButton syncSettingsButton = new JButton("Настройка синхронизации");
		syncSettingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Method method = (Method) syncMethodBox.getSelectedItem();

				Dialog parent = (Dialog) SwingUtilities.windowForComponent(syncSettingsButton);

				SyncSettingsDialog dialog = new SyncSettingsDialog(parent, method.getName());
				dialog.setLocationRelativeTo(parent);
				dialog.showDialog();
			}
		});

		this.syncMethodBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Method method = (Method) syncMethodBox.getSelectedItem();

				if (method == Method.NOSYNC) {
					syncSettingsButton.setEnabled(false);
				} else {
					syncSettingsButton.setEnabled(true);
				}
			}
		});

		if (this.settings.getSyncMethod().equals(Method.NOSYNC.getName())) {
			syncSettingsButton.setEnabled(false);
		}

		JPanel container = new JPanel();
		container.add(this.syncMethodBox);
		container.add(syncSettingsButton);

		this.add(new JLabel(Settings.Options.SYNC_METHOD.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.createGbc(0, rowIndex));
		this.add(container, GridBagLayoutHelper.createGbc(1, rowIndex));
	}
}
