package ru.settletale.client.resource.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.settletale.client.GameClient;
import ru.settletale.client.render.ColladaModelRenderer;
import ru.settletale.client.render.RenderLayer;
import ru.settletale.client.render.ColladaModelRenderer.ColladaGeometryRenderer;
import ru.settletale.client.render.vertex.VertexArrayDataBaker;
import ru.settletale.client.render.vertex.VertexAttribType;
import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.client.resource.collada.Collada;
import ru.settletale.client.resource.collada.Polylist;
import ru.settletale.client.resource.collada.Source;
import ru.settletale.client.resource.collada.Input.Semantic;

public class ColladaLoader extends ResourceLoaderAbstract {
	public static final Map<String, ColladaModelRenderer> MODELS = new HashMap<>();

	static final int POS = 0;
	static final int NORM = 1;
	static final int UV = 2;
	static final int FLAGS = 3;

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "dae" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(resourceFile.path.toFile());

			Collada collada = new Collada(doc);
			ColladaModelRenderer model = new ColladaModelRenderer(collada.asset.upAxis);

			collada.geometries.geometriesList.forEach(geom -> {
				List<RenderLayer> layers = new ArrayList<>();

				geom.mesh.polylists.forEach(pl -> {
					layers.add(generateLayerFromPolylist(pl));
				});

				model.geometries.add(new ColladaGeometryRenderer(geom.name, layers, null));
			});

			ResourceManager.runAfterResourcesLoaded(() -> GameClient.GL_THREAD.addRunnableTask(() -> model.compile()));

			MODELS.put(resourceFile.key, model);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private RenderLayer generateLayerFromPolylist(Polylist polylist) {
		VertexArrayDataBaker baker = new VertexArrayDataBaker(polylist.getTotalUsedVertexCount(), false);
		RenderLayer layer = new RenderLayer(baker);
		baker.addStorage(VertexAttribType.FLOAT_4, POS);
		baker.addStorage(VertexAttribType.INT_1, FLAGS);
		baker.addStorage(VertexAttribType.FLOAT_2, UV);
		baker.addStorage(VertexAttribType.FLOAT_3, NORM);
		layer.setVertexArrayDataBaker(baker);
		
		int posOffset = polylist.getInput(Semantic.VERTEX).offset;
		int normOffset = polylist.usesNormal() ? polylist.getInput(Semantic.NORMAL).offset : -1;

		int inputsCount = polylist.inputs.size();

		Source poses = polylist.mesh.vertices.source;
		Source norms = polylist.mesh.getSource(polylist.getInput(Semantic.NORMAL).source);

		int flags = 0;
		flags |= (false ? 1 : 0) << 8;
		flags |= (polylist.usesNormal() ? 1 : 0) << 9;
		baker.putInt(FLAGS, flags);

		//float[] back = new float[4];
		//back[3] = 1F;

		int vertexIndex = 0;
		Vector4f pos = new Vector4f();
		Vector3f norm = new Vector3f();

		for (int primitive = 0; primitive < polylist.vCounts.capacity(); primitive++) {
			int vertexCount = polylist.vCounts.get(primitive);

			if (vertexCount == 3) {
				for (int v = 0; v < 3; v++) {
					
					// poses.getFloats(polylist.indexes.get(vertexIndex * inputsCount + posOffset),
					// back);

					baker.putFloats(POS, pos);
					// norms.getFloats(polylist.indexes.get(vertexIndex * inputsCount + normOffset),
					// back);
					baker.putFloats(NORM, norm);
					baker.endVertex();
					vertexIndex++;
				}
			}
		}
		
		return layer;
	}
}
