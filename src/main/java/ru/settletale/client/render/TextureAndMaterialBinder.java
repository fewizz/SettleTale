package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;
import ru.settletale.client.gl.UniformBuffer;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class TextureAndMaterialBinder {
	final AdvancedList<Material> materials = new AdvancedArrayList<>();
	final TextureBinder tb = new TextureBinder();
	final UniformBuffer ubo = new UniformBuffer();

	public void setTextureUniformLocation(int location) {
		tb.setCurrentUniformLocation(location);
	}

	public int register(Material material, Texture<?> texture) {
		materials.addIfAbsent(material);
		int index = materials.indexOf(material);
		tb.register(texture, index);
		return index;
	}
	
	public void updateUniforms(ShaderProgram program) {
		if(!ubo.isGenerated()) {
			ubo.gen();
		}
		
		tb.updateUniforms(program);
		
		GL.bindBufferBase(ubo, GlobalUniforms.MATERIALS);
		
		try (MemoryStack ms = MemoryStack.stackPush()) {
			ByteBuffer buff = ms.malloc(materials.size());

			materials.forEach((material) -> {
				material.colorDiffuse.get(buff);
				buff.position(Float.BYTES * 4);
			});
			
			buff.slice();
			ubo.data(buff);
		}
	}
}
