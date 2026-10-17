package jkeypass.importer;

import jkeypass.importer.keepassx.KeePassXImporter;

public enum ImportFormat {
	KEEPASSX_XML("KeePassX (XML)");

	private final String label;

	ImportFormat(String label) {
		this.label = label;
	}

	public AccountImporter newImporter() {
		return switch (this) {
			case KEEPASSX_XML -> new KeePassXImporter();
		};
	}

	@Override
	public String toString() {
		return label;
	}
}
