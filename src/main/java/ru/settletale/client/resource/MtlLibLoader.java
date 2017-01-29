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

		String[] strings = FileUtils.readLines(resourceFile.fullPath);

		MTLLib matFile = new MTLLib();

		Material mat = null;

		for (String str : strings) {
			if (str.startsWith("newmtl")) {			
				mat = new Material();
				mat.name = str.split(" ")[1];
				
				
				matFile.addMaterial(mat);
			}

			if (str.startsWith("Kd")) {
				String[] values = str.split(" ");

				mat.colorDiffuse.x = Float.valueOf(values[1]);
				mat.colorDiffuse.y = Float.valueOf(values[2]);
				mat.colorDiffuse.z = Float.valueOf(values[3]);
			}

			if (str.startsWith("map_Kd")) {
				String[] values = str.split(" ");

				mat.textureNameDiffuse = values[values.length - 1]; // In spec was wrote, that texturename is last (надеюсь правду говороят =P )
			}
		}
		
		MTLS.put(resourceFile.key, matFile);
	}

	@Override
	public void onResourcesLoadEnd() {
		MTLS.forEach((key, mtl) -> {
			mtl.compile();	
		});
	}

}
