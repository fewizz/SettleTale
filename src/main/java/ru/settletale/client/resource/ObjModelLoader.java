package ru.settletale.client.resource;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.render.MTLLib;
import ru.settletale.client.render.Material;
import ru.settletale.client.render.ObjModel;
import ru.settletale.client.render.TextureUnitBinder;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.client.vertex.VertexArrayDataBaker;
import ru.settletale.util.FileUtils;
import ru.settletale.util.StringUtils;

public class ObjModelLoader extends ResourceLoaderAbstract {
	static final int POS = 0;
	static final int NORM = 1;
	static final int UV = 2;
	static final int FLAGS = 3;

	public static final Map<String, ObjModel> MODELS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "obj" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading objModel: " + resourceFile.key);
		String[] strings = FileUtils.readLines(resourceFile.path.toFile());
		float[] back = new float[9];
		int[][] backIndex = new int[6][4];
		int[] backIndexReturn = new int[5];
		float[][] backPos = new float[4][4];
		float[][] backNorm = new float[4][3];
		float[][] backUV = new float[4][2];
		int[] counts = getCountOfElements(strings, new String[] { "v", "vt", "vn" });

		FloatBuffer positions = MemoryUtil.memAllocFloat(counts[0] * 4);
		FloatBuffer uvs = MemoryUtil.memAllocFloat(counts[1] * 2);
		FloatBuffer normals = MemoryUtil.memAllocFloat(counts[2] * 3);

		VertexArrayDataBaker pa = new VertexArrayDataBaker(VertexAttribType.FLOAT_4, VertexAttribType.FLOAT_3, VertexAttribType.FLOAT_2, VertexAttribType.INT_1);

		List<Material> materials = new ArrayList<>();
		TextureUnitBinder tub = new TextureUnitBinder();

		int currentMatID = 0;
		MTLLib currentMTLLib = null;
		Material currentMaterial = null;

		for (int i = 0; i < strings.length; i++) {
			String str = strings[i];

			char firstChar = str.charAt(0);

			if (firstChar == 'v') {
				if (str.startsWith("v ")) {
					readPosition(str, positions, back);
				}
				else if (str.startsWith("vn")) {
					readNormal(str, normals, back);
				}
				else if (str.startsWith("vt")) {
					readUV(str, uvs, back);
				}
			}
			else if (firstChar == 'f') {
				readFace(str, pa, positions, normals, uvs, currentMatID, backIndex, backIndexReturn, backPos, backNorm, backUV);
			}
			else {
				if (str.startsWith("mtllib")) {
					ResourceFile res = resourceFile.dir.getResourceFile(readNameOfMTLLib(str));
					ResourceManager.loadResource(res);
					currentMTLLib = MtlLibLoader.MTLS.get(res.key);
				}
				else if (str.startsWith("usemtl")) {
					String materialName = str.split(" ")[1];
					currentMaterial = currentMTLLib.getMaterial(materialName);
					
					if(!materials.contains(currentMaterial)) {
						materials.add(currentMaterial);
					}
					
					currentMatID = materials.indexOf(currentMaterial);
					tub.use(currentMTLLib.getMaterialTexture(currentMaterial), currentMatID);
				}
			}
		}

		positions.rewind();
		normals.rewind();
		uvs.rewind();

		ObjModel model = new ObjModel();

		MODELS.put(resourceFile.key, model);
	}

	public static void readPosition(String str, FloatBuffer fb, float[] back) {
		int i = StringUtils.readFloats(str, back);

		float x = back[0];
		float y = back[1];
		float z = back[2];
		float w = i == 4 ? back[3] : 1F;

		fb.put(x);
		fb.put(y);
		fb.put(z);
		fb.put(w);
	}

	public static void readNormal(String str, FloatBuffer fb, float[] back) {
		StringUtils.readFloats(str, back);

		float x = back[0];
		float y = back[1];
		float z = back[2];

		fb.put(x);
		fb.put(y);
		fb.put(z);
	}

	public static void readUV(String str, FloatBuffer fb, float[] back) {
		StringUtils.readFloats(str, back);

		float u = back[0];
		float v = back[1];

		fb.put(u);
		fb.put(v);
	}

	public static void readFace(String str, VertexArrayDataBaker pa, FloatBuffer positions, FloatBuffer normals, FloatBuffer uvs, int matID, int[][] back, int[] backReturn, float[][] backPos, float[][] backNorm, float[][] backUV) {
		//Needs to know, if they uses
		back[0][1] = -1; //For UV
		back[0][2] = -1; //For Normal

		int count = StringUtils.readInts(str, back, backReturn, ' ', '/', -1);

		boolean hasUV = back[0][1] != -1;
		boolean hasNormal = back[0][2] != -1;

		boolean isQuad = count == 8 || count == 12;

		if (!hasUV) {
			backUV = null;
		}
		if (!hasNormal) {
			backNorm = null;
		}

		int vertCount = isQuad ? 4 : 3;

		int flags = matID;
		flags |= (hasUV ? 1 : 0) << 8;
		flags |= (hasNormal ? 1 : 0) << 9;
		pa.putInt(FLAGS, flags);

		for (int k = 0; k < vertCount; k++) {
			int v = back[k + 1][0];
			int vt = back[k + 1][1];
			int vn = back[k + 1][2];

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

			if (isQuad) {
				backPos[k][0] = positions.get(posIndex + 0);
				backPos[k][1] = positions.get(posIndex + 1);
				backPos[k][2] = positions.get(posIndex + 2);
				backPos[k][3] = positions.get(posIndex + 3);

				if (hasNormal) {
					backNorm[k][0] = normals.get(normIndex + 0);
					backNorm[k][1] = normals.get(normIndex + 1);
					backNorm[k][2] = normals.get(normIndex + 2);
				}

				if (hasUV) {
					backUV[k][0] = uvs.get(uvIndex + 0);
					backUV[k][1] = uvs.get(uvIndex + 1);
				}
			}
			else {
				pa.putFloat(POS, positions.get(posIndex + 0), positions.get(posIndex + 1), positions.get(posIndex + 2), positions.get(posIndex + 3));
				if (hasNormal)
					pa.putFloat(NORM, normals.get(normIndex + 0), normals.get(normIndex + 1), normals.get(normIndex + 2));
				if (hasUV)
					pa.putFloat(UV, uvs.get(uvIndex + 0), uvs.get(uvIndex + 1));
				pa.endVertex();
			}
		}

		if (isQuad) {
			fillPA(pa, 0, backPos, backNorm, backUV);
			fillPA(pa, 1, backPos, backNorm, backUV);
			fillPA(pa, 2, backPos, backNorm, backUV);

			fillPA(pa, 3, backPos, backNorm, backUV);
			fillPA(pa, 0, backPos, backNorm, backUV);
			fillPA(pa, 2, backPos, backNorm, backUV);
		}
	}

	static void fillPA(VertexArrayDataBaker pa, int indx, float[][] backPos, float[][] backNorm, float[][] backUV) {
		pa.putFloat(POS, backPos[indx][0], backPos[indx][1], backPos[indx][2], backPos[indx][3]);
		if (backNorm != null)
			pa.putFloat(NORM, backNorm[indx][0], backNorm[indx][1], backNorm[indx][2]);
		if (backUV != null)
			pa.putFloat(UV, backUV[indx][0], backUV[indx][1]);
		pa.endVertex();
	}

	public static String readNameOfMTLLib(String str) {
		return str.split(" ")[1];
	}

	public static int[] getCountOfElements(String[] strings, String[] elementNames) {
		int counts[] = new int[elementNames.length];

		for (String str : strings) {
			if (str == null) {
				continue;
			}

			for (int i = 0; i < elementNames.length; i++) {
				if (str.startsWith(elementNames[i])) {
					counts[i]++;
				}
			}
		}

		return counts;
	}

}
