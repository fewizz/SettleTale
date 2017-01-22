package ru.settletale.client.resource;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.ModelObj;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.util.FileUtils;

public class ObjModelLoader extends ResourceLoaderOneExtAbstract {
	static final int POS = 0;
	static final int NORM = 1;
	
	public static final Map<String, ModelObj> MODELS = new HashMap<>();

	@Override
	public String getRequiredExtension() {
		return "obj";
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading objModel: " + resourceFile.key);
		String[] strings = FileUtils.readLines(resourceFile.fullPath);
		
		cleanup(strings);
		
		FloatBuffer positions = MemoryUtil.memAllocFloat(getCountOfElementsSuccessively(strings, "v ") * 4);
		readPositions(strings, positions);
		
		FloatBuffer normals = MemoryUtil.memAllocFloat(getCountOfElementsSuccessively(strings, "vn ") * 3);
		readNormals(strings, normals);
		
		PrimitiveArray pa = new PrimitiveArray(Storage.FLOAT_4, Storage.FLOAT_3);
		readFaces(strings, pa, positions, normals);
		
		ModelObj model = new ModelObj();
		model.setVertexPositions(pa.getVertexCount(), pa.getBuffer(POS), pa.getBuffer(NORM));
		
		MODELS.put(resourceFile.key, model);
	}
	
	@Override
	public void onResourcesLoaded() {
		MODELS.forEach((key, model) -> {
			GLThread.addTask(() -> {
				model.compile();
			});
		});
	}
	
	public static void cleanup(String[] strings) {
		for(int i = 0; i < strings.length; i++) {
			String str = strings[i];
			
			if(isCommentLine(str) || isEmptyLine(str)) {
				strings[i] = null;
			}
		}
	}
	
	public static void readPositions(String[] strings, FloatBuffer fb) {
		int index = 0;
		
		for(int i = 0; i < strings.length; i++) {
			String str = strings[i];
			
			if(str == null || !str.startsWith("v ")) {
				continue;
			}
			
			String[] values = str.split(" ");
			
			float x = Float.valueOf(values[1]);
			float y = Float.valueOf(values[2]);
			float z = Float.valueOf(values[3]);
			float w = values.length == 5 ? Float.valueOf(values[4]) : 1F;
			
			fb.put(index + 0, x);
			fb.put(index + 1, y);
			fb.put(index + 2, z);
			fb.put(index + 3, w);
			
			index += 4;
			strings[i] = null;
		}
	}
	
	public static void readNormals(String[] strings, FloatBuffer fb) {
		int index = 0;
		
		for(int i = 0; i < strings.length; i++) {
			String str = strings[i];
			
			if(str == null || !str.startsWith("vn ")) {
				continue;
			}
			
			String[] values = str.split(" ");
			
			float x = Float.valueOf(values[1]);
			float y = Float.valueOf(values[2]);
			float z = Float.valueOf(values[3]);
			
			fb.put(index + 0, x);
			fb.put(index + 1, y);
			fb.put(index + 2, z);
			
			index += 3;
			strings[i] = null;
		}
	}
	
	public static void readFaces(String[] strings, PrimitiveArray pa, FloatBuffer positions, FloatBuffer normals) {
		for(int i = 0; i < strings.length; i++) {
			String str = strings[i];
			
			if(str == null || !str.startsWith("f ")) {
				continue;
			}
			
			String[] values = str.split(" ");
			
			for(int k = 0; k < 3; k++) {
				String[] values2 = values[k + 1].split("/");
				int v = Integer.parseInt(values2[0]);
				int vn = values2[2].equals("") ? 0 : Integer.parseInt(values2[2]);
				
				int posIndex = 0;
				int normIndex = 0;
				
				if(v < 0) {
					posIndex = positions.capacity() + (v) * 4;
				}
				else {
					posIndex = (v - 1) * 4;
				}
				if(vn < 0) {
					normIndex = normals.capacity() + (vn) * 3;
				}
				else {
					normIndex = (vn - 1) * 3;
				}
				
				pa.data(POS, positions.get(posIndex + 0), positions.get(posIndex + 1), positions.get(posIndex + 2), positions.get(posIndex + 3));
				pa.data(NORM, normals.get(normIndex + 0), normals.get(normIndex + 1), normals.get(normIndex + 2));
				pa.endVertex();
			}
			
			strings[i] = null;
		}
	}
	
	public static int getCountOfElementsSuccessively(String[] strings, String element) {
		int count = 0;
		
		for(String str : strings) {
			if(str == null) {
				continue;
			}
			
			if(str.startsWith(element)) {
				count++;
				continue;
			}
		}
		
		return count;
	}
	
	public static boolean isCommentLine(String str) {
		return str.startsWith("#");
	}
	
	public static boolean isEmptyLine(String str) {
		if(str.isEmpty()) {
			return true;
		}
		
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) != ' ') {
				return false;
			}
		}
		
		return false;
	}
	
}
