package jkeypass.sync.dropbox.gui;

import jkeypass.common.GridBagLayoutHelper;
import jkeypass.sync.dropbox.models.DropboxSettings;

import javax.swing.*;
import java.awt.*;

public class DropboxSettingsPanel extends JPanel implements jkeypass.common.SettingsPanel {
	private DropboxSettings settings;
	private JTextField keyField;
	private JTextField secretField;
	private JTextArea tokenField;

	public DropboxSettingsPanel(jkeypass.common.Settings s) {
		super();

		this.setLayout(new GridBagLayout());
		
		settings = (DropboxSettings) s;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int i = 0;
		add(new JLabel(DropboxSettings.Options.KEY.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
		keyField = new JTextField(settings.getKey());
		add(keyField, GridBagLayoutHelper.gbc(1, i));

		i++;
		add(new JLabel(DropboxSettings.Options.SECRET.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
		secretField = new JTextField(settings.getSecret());
		add(secretField, GridBagLayoutHelper.gbc(1, i));

		i++;
		add(new JLabel(DropboxSettings.Options.TOKEN.getLabel() + ":", JLabel.LEFT), GridBagLayoutHelper.gbc(0, i));
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
