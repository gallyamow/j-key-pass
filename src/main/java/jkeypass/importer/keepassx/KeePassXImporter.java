package jkeypass.importer.keepassx;

import jkeypass.domain.Account;
import jkeypass.importer.AccountImporter;
import jkeypass.importer.ImportException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class KeePassXImporter implements AccountImporter {
	@Override
	public List<Account> importAccounts(Path file) throws ImportException {
		Document document = parse(file);
		NodeList nodes = document.getElementsByTagName("entry");

		List<Account> accounts = new ArrayList<>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;

				accounts.add(new Account(
						text(element, "title"),
						text(element, "username"),
						text(element, "password"),
						text(element, "url"),
						text(element, "comment")));
			}
		}

		return accounts;
	}

	private Document parse(Path file) throws ImportException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file.toFile());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ImportException("Неправильный формат файла", e);
		}
	}

	private String text(Element element, String tag) {
		Node node = element.getElementsByTagName(tag).item(0);
		return node == null ? "" : node.getTextContent();
	}
}
