package ru.settletale.client.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;
import ru.settletale.client.gl.UniformBuffer;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class TexturedMaterialBinder {
	final AdvancedList<Material> materials = new AdvancedArrayList<>();
	final Map<Material, Texture<?>> diffuseTexturesMap = new HashMap<>();
	final Map<Material, Texture<?>> bumpTexturesMap = new HashMap<>();
	final AdvancedList<Texture<?>> textureUnits = new AdvancedArrayList<>();
	final UniformBuffer ubo = new UniformBuffer();
	int diffuseTexturesUniformArraylocation = -1;
	int bumpTexturesUniformArraylocation = -1;

	public void setDiffuseTexturesUniformArraylocation(int location) {
		this.diffuseTexturesUniformArraylocation = location;
	}
	
	public void setBumpTexturesUniformArraylocation(int location) {
		this.bumpTexturesUniformArraylocation = location;
	}

	public int addIfAdsent(Material material, Texture<?> diffTexture, Texture<?> bumpTexture) {
		materials.addIfAbsent(material);
		int matIndex = materials.indexOf(material);
		diffuseTexturesMap.put(material, diffTexture);
		bumpTexturesMap.put(material, bumpTexture);
		
		textureUnits.addIfAbsent(diffTexture);
		textureUnits.addIfAbsent(bumpTexture);
		return matIndex;
	}

	public void bindTextures() {
		textureUnits.forEachIndexed((index, tex) -> GL.bindTextureUnit(index, tex));
	}

	public void updateUniforms(ShaderProgram program) {
		if (!ubo.isGenerated())
			ubo.gen();

		try (MemoryStack ms = MemoryStack.stackPush()) {
			IntBuffer buff = ms.mallocInt(materials.size());

			materials.forEach(material -> {
				int matIndex = materials.indexOf(material);
				int unitIndex = textureUnits.indexOf(diffuseTexturesMap.get(material));
				buff.put(matIndex, unitIndex);
			});

			program.setUniformIntArray(diffuseTexturesUniformArraylocation, buff);
			
			buff.clear();
			materials.forEach(material -> {
				int matIndex = materials.indexOf(material);
				int unitIndex = textureUnits.indexOf(bumpTexturesMap.get(material));
				buff.put(matIndex, unitIndex);
			});

			program.setUniformIntArray(bumpTexturesUniformArraylocation, buff);
		}

		try (MemoryStack ms = MemoryStack.stackPush()) {
			FloatBuffer buff = ms.mallocFloat(materials.size() * 4);

			materials.forEach(material -> {
				buff.put(material.colorDiffuse.x);
				buff.put(material.colorDiffuse.y);
				buff.put(material.colorDiffuse.z);
				buff.put(1F);
			});

			buff.flip();
			ubo.data(buff);
		}

		GL.bindBufferBase(ubo, GlobalUniforms.MATERIALS);
	}
}
