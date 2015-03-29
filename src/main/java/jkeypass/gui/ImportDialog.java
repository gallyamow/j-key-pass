package jkeypass.gui;

import jkeypass.common.GridBagLayoutHelper;
import jkeypass.importation.Import;
import jkeypass.importation.ImportException;
import jkeypass.models.AccountsDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportDialog extends JDialog {
	public static final int CANCEL_OPTION = 0;
	public static final int IMPORT_OPTION = 1;

	private int result = CANCEL_OPTION;

	private JFileChooser chooser;

	public ImportDialog(Frame owner, String title) {
		super(owner, title, true);

		add(filePanel());

		add(buttons(), BorderLayout.SOUTH);

		setResizable(false);
		setLocationByPlatform(true);
		pack();
	}

	public int showDialog() {
		setVisible(true);

		return result;
	}

	public void doImport(AccountsDatabase database) throws ImportException {
		File file = chooser.getSelectedFile();

		Import imp = new Import(database, file, Import.Source.KEEPASSX);
		imp.doImport();
	}

	private JPanel filePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		int rowIndex = 0;

		chooser = new JFileChooser();
		panel.add(chooser, GridBagLayoutHelper.gbc(1, rowIndex));

		return panel;
	}

	private JPanel buttons() {
		JPanel buttonPanel = new JPanel();

		JButton importButton = new JButton("Импортировать");
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = IMPORT_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(importButton);

		JButton closeButton = new JButton("Закрыть");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = CANCEL_OPTION;
				setVisible(false);
			}
		});

		buttonPanel.add(closeButton);

		getRootPane().setDefaultButton(importButton);

		return buttonPanel;
	}
}
