package ru.settletale.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;
import ru.settletale.client.gl.UniformBuffer;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class TextureUnitAndMaterialBinder {
	final AdvancedList<Material> materials = new AdvancedArrayList<>();
	final TextureUnitBinder tub = new TextureUnitBinder();
	final UniformBuffer ubo = new UniformBuffer();
	//int textureUniformLocation = -1;
	//int materialUniformLocation = -1;

	public TextureUnitAndMaterialBinder() {
	}

	public void setTextureUniformLocation(int location) {
		//textureUniformLocation = location;
		tub.setCurrentUniformLocation(location);
	}
	
	/*public void setMaterialUniformLocation(int location) {
		materialUniformLocation = location;
		tub.setCurrentUniformLocation(textureUniformLocation);
	}*/

	public void use(Material m, Texture<?> t) {
		//if (textureUniformLocation == -1) {
		//	throw new Error("Texture uniform location not set");
		//}
		//if (materialUniformLocation == -1) {
		//	throw new Error("Material uniform location not set");
		//}

		materials.addIfAbsent(m);
		int index = materials.indexOf(m);

		tub.use(t, index);
	}
	
	public void updateUniforms(ShaderProgram program) {
		if(!ubo.isGenerated()) {
			ubo.gen();
		}
		
		tub.updateUniforms(program);
		
		GL.bindBufferBase(ubo, GlobalUniforms.MATERIALS);
		
		try (MemoryStack ms = MemoryStack.stackPush()) {
			ByteBuffer buff = ms.malloc(size)

			array.forEach((position, index) -> buff.put(position, index));

			program.setUniformIntArray(location, buff);
		}
	}
}
