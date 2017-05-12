package ru.settletale.client.resource;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.render.Material;
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

		String[] strings = FileUtils.readLines(resourceFile.path.toFile());

		MTLLib matLib = new MTLLib();

		Material mat = null;

		for (String str : strings) {
			if(str.length() == 0)
				continue;
			
			for(;;) {
				if(str.length() < 1) {
					break;
				}
				
				char firstChar = str.charAt(0);
				
				if(firstChar == '\t' || firstChar == ' ') {
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
				ResourceFile res = resourceFile.dir.getResourceFileIncludingSubdirectories(textureDiffuse);
				ResourceManager.loadResource(res);

				matLib.addTextureToMaterial(mat, TextureLoader.TEXTURES.get(res.key));
			}
		}

		MTLS.put(resourceFile.key, matLib);
	}
}
