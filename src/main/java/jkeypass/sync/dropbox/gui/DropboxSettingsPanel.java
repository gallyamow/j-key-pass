package jkeypass.sync.dropbox.gui;

import jkeypass.gui.GridBagPanel;
import jkeypass.sync.dropbox.models.DropboxSettings;

import javax.swing.*;

public class DropboxSettingsPanel extends GridBagPanel implements jkeypass.common.SettingsPanel {
	private DropboxSettings settings;
	private JTextField keyField;
	private JTextField secretField;
	private JTextArea tokenField;

	public DropboxSettingsPanel(jkeypass.common.Settings settings) {
		super();

		this.settings = (DropboxSettings) settings;

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int i = 0;
		this.add(new JLabel(DropboxSettings.Options.KEY.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));
		this.keyField = new JTextField(this.settings.getKey());
		this.add(this.keyField, this.createGbc(1, i));

		i++;
		this.add(new JLabel(DropboxSettings.Options.SECRET.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));
		this.secretField = new JTextField(this.settings.getSecret());
		this.add(this.secretField, this.createGbc(1, i));

		i++;
		this.add(new JLabel(DropboxSettings.Options.TOKEN.getLabel() + ":", JLabel.LEFT), this.createGbc(0, i));
		this.tokenField = new JTextArea(this.settings.getToken(), 3, 30);
		this.add(this.tokenField, this.createGbc(1, i));
	}

	@Override
	public jkeypass.common.Settings getUpdatedSettings() {
		this.settings.setKey(this.keyField.getText());
		this.settings.setSecret(this.secretField.getText());
		this.settings.setToken(this.tokenField.getText());

		return this.settings;
	}
}
