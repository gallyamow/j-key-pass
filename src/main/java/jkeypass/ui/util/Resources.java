package jkeypass.ui.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public final class Resources {
	private Resources() {
	}

	public static ImageIcon icon(String name) {
		return new ImageIcon(url(name));
	}

	public static Image image(String name) {
		try {
			return ImageIO.read(url(name));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static URL url(String name) {
		URL url = Resources.class.getResource("/icons/" + name);
		if (url == null) {
			throw new IllegalStateException("Missing resource: /icons/" + name);
		}
		return url;
	}
}
