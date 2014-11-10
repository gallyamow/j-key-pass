package jkeypass.gui;

import jkeypass.models.Settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends GridBagPanel {
	private Settings settings;
	private JComboBox<UIManager.LookAndFeelInfo> themeBox;

	public String getTheme() {
		return ((UIManager.LookAndFeelInfo) this.themeBox.getSelectedItem()).getClassName();
	}

	public SettingsPanel(Settings settings) {
		this.settings = settings;

		int i = 0;

		this.createThemeBox(++i);
	}

	private void createThemeBox(int rowIndex) {
		this.add(new JLabel(Settings.Options.THEME.getLabel() + ":", JLabel.LEFT), this.createGbc(0, rowIndex));

		this.themeBox = new JComboBox<>();

		themeBox.setRenderer(new ListCellRenderer<UIManager.LookAndFeelInfo>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends UIManager.LookAndFeelInfo> list, UIManager.LookAndFeelInfo value, int index, boolean isSelected, boolean cellHasFocus) {
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

		this.add(themeBox, this.createGbc(1, rowIndex));
	}
}
