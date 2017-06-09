package ru.settletale.client.render;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Shader.Type;
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
			PROGRAM.attachShader(new Shader().gen(Type.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/collada/collada.vs")));
			PROGRAM.attachShader(new Shader().gen(Type.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/collada/collada.fs")));
			PROGRAM.link();
		}
		
		for(ColladaGeometryRenderer g : geometries) {
			for(RenderLayer l : g.layers) {
				l.setShaderProgram(PROGRAM);
				l.compile();
				l.getVertexArrayDataBaker().delete();
			}
		}
	}
	
	public void render() {
		for(ColladaGeometryRenderer g : geometries) {
			for(RenderLayer l : g.layers) {
				l.render(GL11.GL_TRIANGLES);
			}
		}
	}
	
	public static class ColladaGeometryRenderer {
		final String name;
		final List<RenderLayer> layers;
		final Matrix4f matrix;

		public ColladaGeometryRenderer(String name, List<RenderLayer> layers, Matrix4f mat) {
			this.name = name;
			this.layers = layers;
			this.matrix = mat;
		}
	}
}
