package jkeypass.sync.dropbox.gui;

import jkeypass.common.GridBagLayoutHelper;
import jkeypass.sync.dropbox.models.Settings;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel implements jkeypass.common.SettingsPanel {
	private Settings settings;
	private JTextField keyField;
	private JTextField secretField;
	private JTextArea tokenField;

	public SettingsPanel(jkeypass.common.Settings s) {
		super();

		this.setLayout(new GridBagLayout());
		
		settings = (Settings) s;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int i = 0;
		add(new JLabel(Settings.Options.KEY.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
		keyField = new JTextField(settings.getKey());
		add(keyField, GridBagLayoutHelper.gbc(1, i));

		i++;
		add(new JLabel(Settings.Options.SECRET.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
		secretField = new JTextField(settings.getSecret());
		add(secretField, GridBagLayoutHelper.gbc(1, i));

		i++;
		add(new JLabel(Settings.Options.TOKEN.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
		tokenField = new JTextArea(settings.getToken(), 3, 30);
		add(tokenField, GridBagLayoutHelper.gbc(1, i));
	}

	@Override
	public jkeypass.common.Settings getUpdatedSettings() {
		settings.setKey(keyField.getText());
		settings.setSecret(secretField.getText());
		settings.setToken(tokenField.getText());

		return settings;
	}
}
