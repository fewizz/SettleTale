package ru.settletale.client.resource.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.render.Material;
import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.client.Client;
import ru.settletale.client.gl.Texture;
import ru.settletale.client.render.MTLLib;
import ru.settletale.util.FileUtils;

public class MtlLibLoader extends ResourceLoaderAbstract {
	public static final Map<String, MTLLib> MTLS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "mtl" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading objMaterial: " + resourceFile.key);

		List<String> strings = FileUtils.readLines(resourceFile.path.toFile());

		MTLLib matLib = new MTLLib();

		Material mat = null;

		for (String str : strings) {
			if (str.length() == 0)
				continue;

			for (;;) {
				if (str.length() < 1) {
					break;
				}

				char firstChar = str.charAt(0);

				if (firstChar == '\t' || firstChar == ' ') {
					str = str.substring(1);
				}
				else {
					break;
				}
			}

			if (str.startsWith("newmtl")) {
				mat = new Material();
				String name = str.split(" ")[1];
				matLib.addMaterial(mat, name);
			}

			if (str.startsWith("Kd")) {
				String[] values = str.split(" ");

				mat.colorDiffuse.x = Float.valueOf(values[1]);
				mat.colorDiffuse.y = Float.valueOf(values[2]);
				mat.colorDiffuse.z = Float.valueOf(values[3]);
			}

			if (str.startsWith("map_Kd")) {
				String[] values = str.split(" ");

				String textureDiffuse = values[values.length - 1];
				ResourceFile res = resourceFile.dir.findFileIncludingSubdirectories(textureDiffuse);
				ResourceManager.loadResource(res);

				matLib.addDiffuseTextureToMaterial(mat, TextureLoader.TEXTURES.get(res.key));
			}

			if (str.startsWith("map_bump")) {
				String[] values = str.split(" ");

				String textureBump = values[values.length - 1];
				ResourceFile res = resourceFile.dir.findFileIncludingSubdirectories(textureBump);
				ResourceManager.loadResource(res);

				Texture<?> tex = TextureLoader.TEXTURES.get(res.key);
				matLib.addBumpTextureToMaterial(mat, tex);

				if (tex != null) {
					Client.GL_THREAD.addTask(() -> {
						tex.parameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
						tex.parameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
					});
				}
			}
		}

		MTLS.put(resourceFile.key, matLib);
	}
}
