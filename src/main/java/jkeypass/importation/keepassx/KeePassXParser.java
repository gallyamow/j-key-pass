package jkeypass.importation.keepassx;

import jkeypass.importation.ImportException;
import jkeypass.importation.Parser;
import jkeypass.models.Account;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class KeePassXParser implements Parser {
	public ArrayList<Account> getList(File file) throws ImportException {
		Document document = getDomDocument(file);

		if (document == null) {
			throw new ImportException("Неправильный формат файла", null);
		}

		return parseDocument(document);
	}

	private Document getDomDocument(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
		} catch (ParserConfigurationException | SAXException | IOException ignored) {
		}

		return document;
	}

	private ArrayList<Account> parseDocument(Document document) {
		NodeList nodes = document.getElementsByTagName("entry");

		ArrayList<Account> result = new ArrayList<>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				String title = element.getElementsByTagName("title").item(0).getTextContent();
				String username = element.getElementsByTagName("username").item(0).getTextContent();
				String password = element.getElementsByTagName("password").item(0).getTextContent();
				String url = element.getElementsByTagName("url").item(0).getTextContent();
				String comment = element.getElementsByTagName("comment").item(0).getTextContent();

				Account account = new Account(title, username, password, url, comment);

				result.add(account);
			}
		}

		return result;
	}
}
