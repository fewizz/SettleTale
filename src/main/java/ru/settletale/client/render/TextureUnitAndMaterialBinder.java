package ru.settletale.client.render;

import ru.settletale.client.gl.Texture;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class TextureUnitAndMaterialBinder {
	final AdvancedList<Material> materials = new AdvancedArrayList<>();
	final TextureUnitBinder tub = new TextureUnitBinder();
	int textureUniformLocation = -1;
	
	public TextureUnitAndMaterialBinder() {
		
	}
	
	public void use(Material m, Texture<?> t) {
		if(textureUniformLocation == -1) {
			throw new Error("Texture uniform location not set");
		}
		
		materials.addIfAbsent(m);
		int index = materials.indexOf(m);
		
		tub.use(t, index);
	}
}
