package ru.settletale.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.util.FileUtils;

public class RenderLayerList {
	static final int POS = 0;
	static final int NORM = 1;
	static final int UV = 2;
	static final int COLOR = 3;
	static final int FLAGS = 4;
	
	static final Map<Integer, ShaderProgram> PROGRAMS = new HashMap<>();
	static String[] vsTemplate;
	static String[] fsTemplate;
	
	boolean normal;
	boolean color;
	boolean uv;
	int materialID = 0;
	final List<RenderLayer> layers;
	RenderLayer currentLayer;
	
	public RenderLayerList() {
		layers = new ArrayList<>();
		addAndSetNewEmptyLayer();
	}
	
	public void addAndSetNewEmptyLayer() {
		RenderLayer layer = new RenderLayer();
		layers.add(layer);
		currentLayer = layer;
	}
	
	public void compile() {
		layers.forEach(layer -> {
			layer.program = generateOrGetShaderProgram(layer);
			layer.compile();
		});
	}
	
	public void render(int mode) {
		layers.forEach(layer -> {
			layer.render(mode);
		});
	}
	
	public RenderLayerList position(float x, float y, float z) {
		currentLayer.primitiveArray.data(POS, x, y, z, 1);
		return this;
	}
	
	public RenderLayerList color(byte r, byte g, byte b, byte a) {
		currentLayer.primitiveArray.data(COLOR, r, g, b, a);
		color = true;
		currentLayer.usedColor = true;
		return this;
	}
	
	public void uv(float u, float v) {
		currentLayer.primitiveArray.data(UV, u, v);
		uv = true;
		currentLayer.usedUV = true;
	}
	
	public void normal(float x, float y, float z) {
		currentLayer.primitiveArray.data(NORM, x, y, z);
		normal = true;
		currentLayer.usedNormal = true;
	}
	
	public void material(Material mat) {
		if(!currentLayer.materials.contains(mat)) {
			currentLayer.materials.add(mat);
		}
		materialID = currentLayer.materials.indexOf(mat);
		currentLayer.usedMaterials = true;
	}
	
	public void materialID(int matID) {
		materialID = matID;
		currentLayer.usedMaterials = true;
	}
	
	public void endVertex() {
		int flags = normal ? 1 : 0;
		flags |= (color ? 1 : 0) << 1;
		flags |= (uv ? 1 : 0) << 2;
		flags |= materialID << 3;
		
		currentLayer.primitiveArray.data(FLAGS, flags);
		
		currentLayer.primitiveArray.endVertex();
		
		currentLayer.vertexCount++;
		
		normal = false;
		color = false;
		uv = false;
	}
	
	ShaderProgram generateOrGetShaderProgram(RenderLayer layer) {
		ShaderProgram prog = PROGRAMS.get(layer.hashCode());
		
		if(prog != null) {
			return prog;
		}
		
		prog = new ShaderProgram().gen();
		
		if(vsTemplate == null || fsTemplate == null) {
			vsTemplate = FileUtils.readLines(ResourceManager.getResourceFile("shaders/render_list.vst").fullPath);
			fsTemplate = FileUtils.readLines(ResourceManager.getResourceFile("shaders/render_list.fst").fullPath);
		}
		
		String vs = format(layer, vsTemplate);
		String fs = format(layer, fsTemplate);
		
		
		prog.attachShader(new Shader(Shader.Type.VERTEX, vs).gen().compile());
		prog.attachShader(new Shader(Shader.Type.FRAGMENT, fs).gen().compile());
		prog.link();
		
		PROGRAMS.put(layer.hashCode(), prog);
		
		return prog;
	}
	
	String format(RenderLayer layer, String[] template) {
		StringBuilder builder = new StringBuilder();
		
		for(String str : template) {
			if(layer.usedNormal && str.contains("@N")) {
				builder.append(str.replace("@N", ""));
			}
			if(layer.usedUV && str.contains("@T")) {
				builder.append(str.replace("@T", ""));
			}
			if(layer.usedColor && str.contains("@C")) {
				builder.append(str.replace("@C", ""));
			}
			if(layer.usedMaterials && str.contains("@M")) {
				builder.append(str.replace("@M", ""));
			}
			if(!str.contains("@")) { 
				builder.append(str);
			}
			
			builder.append("\n");
		}
		
		return builder.toString();
	}
}
