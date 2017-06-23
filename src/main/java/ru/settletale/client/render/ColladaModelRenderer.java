package ru.settletale.client.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Shader.ShaderType;
import ru.settletale.client.render.vertex.VertexArrayDataBaker;
import ru.settletale.client.render.vertex.VertexArrayRenderer;
import ru.settletale.client.render.vertex.VertexArrayRenderer.GLDrawFunc;
import ru.settletale.client.resource.collada.Asset.UpAxis;
import ru.settletale.client.resource.loader.ShaderSourceLoader;

public class ColladaModelRenderer {
	static final ShaderProgram PROGRAM = new ShaderProgram();
	public final UpAxis upAxis;
	public final List<ColladaGeometryRenderer> geometries = new ArrayList<ColladaModelRenderer.ColladaGeometryRenderer>();
	
	public ColladaModelRenderer(UpAxis upAxis) {
		this.upAxis = upAxis;
	}
	
	public void compile() {
		if(!PROGRAM.isGenerated()) {
			PROGRAM.gen();
			PROGRAM.attachShader(new Shader().gen(ShaderType.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/collada/collada.vs")));
			PROGRAM.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/collada/collada.fs")));
			PROGRAM.link();
		}
		
		for(ColladaGeometryRenderer g : geometries) {
			/*for(VertexArrayRenderer layer : g.layers) {
				layer.setShaderProgram(PROGRAM);
				layer.compile();
				layer.getVertexArrayDataBaker().delete();
			}*/
			g.layers.forEach((renderer, baker) -> {
				renderer.setShaderProgram(PROGRAM);
				renderer.compile(baker);
			});
		}
	}
	
	public void render() {
		for(ColladaGeometryRenderer g : geometries) {
			for(VertexArrayRenderer l : g.layers.keySet()) {
				l.program.setUniformMatrix4f(0, g.matrix);
				l.render(GLDrawFunc.DRAW_ARRAYS, GL11.GL_TRIANGLES);
			}
		}
	}
	
	public static class ColladaGeometryRenderer {
		final String name;
		final Map<VertexArrayRenderer, VertexArrayDataBaker> layers;
		final Matrix4f matrix;

		public ColladaGeometryRenderer(String name, Map<VertexArrayRenderer, VertexArrayDataBaker> layers, Matrix4f mat) {
			this.name = name;
			this.layers = layers;
			this.matrix = mat;
		}
	}
}
