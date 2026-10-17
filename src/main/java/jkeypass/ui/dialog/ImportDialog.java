package jkeypass.ui.dialog;

import jkeypass.importer.ImportFormat;
import jkeypass.ui.util.GridBagLayoutHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public final class ImportDialog extends BaseDialog {
	private final JTextField fileField = new JTextField(20);
	private final JComboBox<ImportFormat> formatBox = new JComboBox<>(ImportFormat.values());

	public ImportDialog(Window owner) {
		super(owner, "Импорт");

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		fileField.setEditable(false);

		JButton browseButton = new JButton("Обзор...");
		browseButton.addActionListener(e -> chooseFile());

		JPanel filePanel = new JPanel(new BorderLayout(5, 0));
		filePanel.add(fileField, BorderLayout.CENTER);
		filePanel.add(browseButton, BorderLayout.EAST);

		panel.add(new JLabel("Файл:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, 0));
		panel.add(filePanel, GridBagLayoutHelper.gbc(1, 0));
		panel.add(new JLabel("Формат:", JLabel.LEFT), GridBagLayoutHelper.gbc(0, 1));
		panel.add(formatBox, GridBagLayoutHelper.gbc(1, 1));

		add(panel);
		add(buttons("Импортировать"), BorderLayout.SOUTH);

		setResizable(false);
		pack();
		setLocationRelativeTo(owner);
	}

	@Override
	protected void accept() {
		if (fileField.getText().isBlank()) {
			return;
		}
		super.accept();
	}

	public Path file() {
		return Path.of(fileField.getText());
	}

	public ImportFormat format() {
		return (ImportFormat) formatBox.getSelectedItem();
	}

	private void chooseFile() {
		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			fileField.setText(file.getAbsolutePath());
		}
	}
}
