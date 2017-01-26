package ru.settletale.client.resource;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.opengl.Texture2D;
import ru.settletale.client.render.ObjMTL;
import ru.settletale.util.FileUtils;
import ru.settletale.util.StringUtils;

public class ObjMtlLoader extends ResourceLoaderAbstract {
	public static final Map<String, ObjMTL> MTLS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "mtl" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading objMaterial: " + resourceFile.key);

		String[] strings = FileUtils.readLines(resourceFile.fullPath);

		ObjMTL matFile = new ObjMTL();

		ObjMTL.Material mat = null;

		for (String str : strings) {
			if (StringUtils.isEmptyOrSpacedLine(str) || StringUtils.isSharpCommentLine(str)) {
				continue;
			}

			if (str.startsWith("newmtl")) {
				mat = new ObjMTL.Material();
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
	}

	@Override
	public void onResourcesLoadEnd() {
		MTLS.forEach((key, mtl) -> {
			for (ObjMTL.Material mat : mtl.materials.values()) {
				if (mat.textureDiffuse != null) {
					Texture2D tex = TextureLoader.TEXTURES.get(mat.textureNameDiffuse);
					
					if(tex == null) {
						throw new Error("Disffuse texture \"" + mat.textureNameDiffuse + "\" for material \"" + mat.name + "\" wasn't fount");
					}
				}
			}
		});
	}

}
