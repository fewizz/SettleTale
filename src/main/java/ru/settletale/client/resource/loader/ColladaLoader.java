package ru.settletale.client.resource.loader;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ru.settletale.client.Client;
import ru.settletale.client.render.ColladaModelRenderer;
import ru.settletale.client.render.ColladaModelRenderer.ColladaGeometryRenderer;
import ru.settletale.client.render.GlobalUniforms;
import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.client.resource.collada.Collada;
import ru.settletale.client.resource.collada.Polylist;
import ru.settletale.client.resource.collada.Source;
import ru.settletale.client.resource.collada.TransformationElement;
import ru.settletale.memory.MemoryBlock;
import wrap.gl.GL;
import wrap.gl.UniformBuffer;
import ru.settletale.client.resource.collada.Input.Semantic;
import ru.settletale.client.resource.collada.Material;
import ru.settletale.client.resource.collada.Matrix;

public class ColladaLoader extends ResourceLoaderAbstract<ColladaModelRenderer> {
	//public static final Map<String, ColladaModelRenderer> MODELS = new HashMap<>();

	/*static final int POS = 0;
	static final int NORM = 1;
	static final int UV = 2;
	static final int FLAGS = 3;*/

	@Override
	public ColladaModelRenderer loadResource(ResourceFile resourceFile) {
		
		
		
		/*try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(resourceFile.path.toFile());

			Collada collada = new Collada(doc);
			ColladaModelRenderer model = new ColladaModelRenderer(collada.asset.upAxis);

			collada.visualScenesList.forEach(vs -> vs.nodes.forEach(node -> {
				Map<VertexArrayRenderer, VertexArrayDataBaker> layers = new HashMap<>();

				node.geometyInstances.forEach(gi -> {
					gi.geometry.mesh.polylists.forEach(pl -> {
						generateLayerFromPolylist(pl, layers, gi.material);
					});
				});
				
				Matrix4f mat = null;
				for(TransformationElement e : node.transformElements) {
					if(e instanceof Matrix) {
						mat = ((Matrix) e).matrix;
						break;
					}
				}

				model.geometries.add(new ColladaGeometryRenderer(vs.name, layers, mat));
			}));

			ResourceManager.runAfterResourcesLoaded(() -> Client.GL_THREAD.execute(() -> model.compile()));

			MODELS.put(resourceFile.key, model);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}*/
	}

	/*private VertexArrayRenderer generateLayerFromPolylist(Polylist polylist, Map<VertexArrayRenderer, VertexArrayDataBaker> layers, Material m) {
		VertexArrayDataBaker baker = new VertexArrayDataBaker(polylist.getTotalUsedVertexCount(), false);
		VertexArrayRenderer layer = new VertexArrayRenderer();//.setVertexArrayDataBaker(baker);
		layers.put(layer, baker);
		
		FloatBuffer matMemoryBlock = MemoryUtil.memAllocFloat(4);
		matMemoryBlock.put(0, m.effect.phong.diffuse.x);
		matMemoryBlock.put(1, m.effect.phong.diffuse.y);
		matMemoryBlock.put(2, m.effect.phong.diffuse.z);
		matMemoryBlock.put(3, m.effect.phong.diffuse.w);
		UniformBuffer ubo = new UniformBuffer();
		
		Client.GL_THREAD.execute(() -> {
			ubo.gen();
			ubo.data(matMemoryBlock);
		});
		
		layer.onPreRender(() -> {
			GL.bindBufferBase(ubo, GlobalUniforms.MATERIALS);
		});
		
		Source verts = polylist.mesh.vertices.source;
		Source norms = polylist.mesh.getSource(polylist.getInput(Semantic.NORMAL).source);
		
		baker.addAttrib(VertexAttribType.FLOAT_4, POS);
		baker.addAttrib(VertexAttribType.INT_1, FLAGS);
		//baker.addAttrib(VertexAttribType.FLOAT_2, UV);
		baker.addAttrib(VertexAttribType.FLOAT_3, NORM);
		
		int posOffset = polylist.getInput(Semantic.VERTEX).offset;
		int normOffset = polylist.usesNormal() ? polylist.getInput(Semantic.NORMAL).offset : -1;

		int inputsCount = polylist.inputs.size();

		int flags = 0;
		//flags |= (false ? 1 : 0) << 8;
		flags |= (polylist.usesNormal() ? 1 : 0) << 0;
		baker.putInt(FLAGS, flags);

		MemoryBlock mbPos = baker.getAttribArrayData(POS).getCurrentAttribMemoryBlock();
		mbPos.putFloatF(3, 1.0F); // If stride is 3
		MemoryBlock mbNorm = baker.getAttribArrayData(NORM).getCurrentAttribMemoryBlock();
		
		int vertAttribBytes = verts.array.primitiveType.bytes * verts.accessor.stride;
		int normAttribBytes = Float.BYTES * 3;
		
		int vertexIndex = 0;

		for (int primitive = 0; primitive < polylist.vCounts.capacity(); primitive++) {
			int vertexCount = polylist.vCounts.get(primitive);

			if (vertexCount == 3) {
				for (int v = 0; v < 3; v++) {
					verts.array.memoryBlock.copyTo(mbPos, polylist.indexes.get(vertexIndex * inputsCount + posOffset) * vertAttribBytes, 0, vertAttribBytes);
					norms.array.memoryBlock.copyTo(mbNorm, polylist.indexes.get(vertexIndex * inputsCount + normOffset) * normAttribBytes, 0, normAttribBytes);
					
					baker.endVertex();
					vertexIndex++;
				}
			}
		}
		
		return layer;
	}*/
}
