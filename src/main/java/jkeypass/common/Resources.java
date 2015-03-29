package jkeypass.common;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Resources {
	public ImageIcon getIcon(String name) {
		URL url = getClass().getClassLoader().getResource("icons/" + name);
		return new ImageIcon(url);
	}

	public Image getImage(String name) {
		URL url = getClass().getClassLoader().getResource("icons/" + name);
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
}
