package ru.settletale.client.resource;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.ObjModel;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.util.FileUtils;
import ru.settletale.util.StringUtils;

public class ObjModelLoader extends ResourceLoaderAbstract {
	static final int POS = 0;
	static final int NORM = 1;
	static final int UV = 2;
	static final int MAT_ID = 3;

	public static final Map<String, ObjModel> MODELS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "obj" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading objModel: " + resourceFile.key);
		String[] strings = FileUtils.readLines(resourceFile.fullPath);
		ObjModel model = new ObjModel();

		cleanup(strings);

		FloatBuffer positions = MemoryUtil.memAllocFloat(getCountOfElementsSuccessively(strings, "v ") * 4);
		FloatBuffer uvs = MemoryUtil.memAllocFloat(getCountOfElementsSuccessively(strings, "vt ") * 2);
		FloatBuffer normals = MemoryUtil.memAllocFloat(getCountOfElementsSuccessively(strings, "vn ") * 3);
		PrimitiveArray pa = new PrimitiveArray(Storage.FLOAT_4, Storage.FLOAT_3, Storage.FLOAT_2, Storage.BYTE_1);

		String mtlLibName = "";
		List<String> materials = new ArrayList<String>();
		int currentMatID = 0;

		for (int i = 0; i < strings.length; i++) {
			String str = strings[i];

			if (str == null) {
				continue;
			}

			if (str.startsWith("v ")) {
				readPosition(str, positions);
			}

			if (str.startsWith("vn ")) {
				readNormal(str, normals);
			}
			
			if (str.startsWith("vt ")) {
				readUV(str, uvs);
			}

			if (str.startsWith("mtllib ")) {
				mtlLibName = readNameOfMTLLib(str);
			}

			if (str.startsWith("usemtl ")) {
				currentMatID = getIDOfMaterial(str, materials);
			}

			if (str.startsWith("f ")) {
				readFace(str, pa, positions, normals, uvs, currentMatID);
			}
		}

		positions.rewind();
		normals.rewind();
		uvs.rewind();
		
		model.setVertexInfo(pa.getVertexCount(), pa.getBuffer(POS), pa.getBuffer(NORM), pa.getBuffer(UV), pa.getBuffer(MAT_ID));
		model.setMtlLibName(mtlLibName);
		model.setMaterials(materials);

		MODELS.put(resourceFile.key, model);
	}

	@Override
	public void onResourcesLoadEnd() {
		MODELS.forEach((key, model) -> {
			GLThread.addTask(() -> {
				model.compile();
			});
		});
	}

	public static void cleanup(String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			String str = strings[i];

			if (StringUtils.isSharpCommentLine(str) || StringUtils.isEmptyOrSpacedLine(str)) {
				strings[i] = null;
			}
		}
	}

	public static void readPosition(String str, FloatBuffer fb) {
		String[] values = str.split(" ");

		float x = Float.valueOf(values[1]);
		float y = Float.valueOf(values[2]);
		float z = Float.valueOf(values[3]);
		float w = values.length == 5 ? Float.valueOf(values[4]) : 1F;

		fb.put(x);
		fb.put(y);
		fb.put(z);
		fb.put(w);
	}

	public static void readNormal(String str, FloatBuffer fb) {
		String[] values = str.split(" ");

		float x = Float.valueOf(values[1]);
		float y = Float.valueOf(values[2]);
		float z = Float.valueOf(values[3]);

		fb.put(x);
		fb.put(y);
		fb.put(z);
	}

	public static void readUV(String str, FloatBuffer fb) {
		String[] values = str.split(" ");

		float u = Float.valueOf(values[1]);
		float v = Float.valueOf(values[2]);

		fb.put(u);
		fb.put(v);
	}
	
	public static void readFace(String str, PrimitiveArray pa, FloatBuffer positions, FloatBuffer normals, FloatBuffer uvs, int matID) {
		String[] values = str.split(" ");

		for (int k = 0; k < 3; k++) {
			String[] values2 = values[k + 1].split("/");
			int v = Integer.parseInt(values2[0]);
			boolean hasUV = !values2[1].equals("");
			int vt = hasUV ? Integer.parseInt(values2[1]) : 0;
			int vn = values2[2].equals("") ? 0 : Integer.parseInt(values2[2]);

			int posIndex = 0;
			int normIndex = 0;
			int uvIndex = 0;

			if (v < 0) {
				posIndex = positions.position() + (v * 4);
			}
			else {
				posIndex = (v - 1) * 4;
			}
			if (vn < 0) {
				normIndex = normals.position() + (vn * 3);
			}
			else {
				normIndex = (vn - 1) * 3;
			}
			if (vt < 0) {
				uvIndex = uvs.position() + (vt * 2);
			}
			else {
				uvIndex = (vt - 1) * 2;
			}

			pa.data(POS, positions.get(posIndex + 0), positions.get(posIndex + 1), positions.get(posIndex + 2), positions.get(posIndex + 3));
			pa.data(NORM, normals.get(normIndex + 0), normals.get(normIndex + 1), normals.get(normIndex + 2));
			if(hasUV)
				pa.data(UV, uvs.get(uvIndex + 0), uvs.get(uvIndex + 1));
			pa.data(MAT_ID, matID);
			pa.endVertex();
		}

	}

	public static String readNameOfMTLLib(String str) {
		return str.split(" ")[1];
	}

	public static int getIDOfMaterial(String str, List<String> materials) {
		String m1 = str.split(" ")[1];
		
		if(!materials.contains(m1)) {
			materials.add(m1);
		}

		return materials.indexOf(m1);
	}

	public static int getCountOfElementsSuccessively(String[] strings, String element) {
		int count = 0;

		for (String str : strings) {
			if (str == null) {
				continue;
			}

			if (str.startsWith(element)) {
				count++;
				continue;
			}
		}

		return count;
	}

}
