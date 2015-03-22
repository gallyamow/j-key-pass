package jkeypass.common;

import javax.swing.*;
import java.net.URL;

public class Resources {
	public ImageIcon getIcon(String name) {
		URL url = getClass().getClassLoader().getResource("icons/" + name);
		return new ImageIcon(url);
	}
}
