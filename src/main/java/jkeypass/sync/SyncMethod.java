package jkeypass.sync;

public enum SyncMethod {
	NONE("nosync", "Без синхронизации"),
	DROPBOX("dropbox", "Dropbox");

	private final String id;
	private final String label;

	SyncMethod(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String id() {
		return id;
	}

	public String label() {
		return label;
	}

	public static SyncMethod fromId(String id) {
		for (SyncMethod method : values()) {
			if (method.id.equals(id)) {
				return method;
			}
		}

		return NONE;
	}

	@Override
	public String toString() {
		return label;
	}
}
