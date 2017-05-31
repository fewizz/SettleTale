package ru.settletale.client.resource.loader;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.settletale.client.render.ColladaModel;
import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.collada.Collada;
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

			Collada collada = new Collada(XMLUtils.getFirstChildElement("COLLADA", doc)).loadGeometries();
			ColladaModel model = new ColladaModel();
			
			int vertexCount = 0;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}
