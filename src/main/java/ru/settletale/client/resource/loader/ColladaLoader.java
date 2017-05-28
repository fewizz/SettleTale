package ru.settletale.client.resource.loader;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import ru.settletale.client.resource.ResourceFile;
import ru.settletale.util.XMLUtils;

public class ColladaLoader extends ResourceLoaderAbstract {

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "dae" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(resourceFile.path.toFile());

			Node colladaNode = XMLUtils.getFirstChildNodeWithName(doc, "COLLADA");
			Node geometriesNode = XMLUtils.getFirstChildNodeWithName(colladaNode, "library_geometries");

			XMLUtils.forEachChildNode(geometriesNode, ColladaLoader::parseGeometry);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	static void parseGeometry(Node geometryNode) {
		XMLUtils.forEachChildNode(geometryNode, node -> {
			
		});
	}
}
