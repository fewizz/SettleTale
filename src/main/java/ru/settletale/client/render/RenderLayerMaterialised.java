package ru.settletale.client.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.vertex.VertexAttribType;

public class RenderLayerMaterialised extends RenderLayer {
	final Material[] materials;
	IntBuffer diffuseTextures;
	FloatBuffer diffuseColors;
	int materialCount = 0;

	public RenderLayerMaterialised(VertexAttribType... storages) {
		super(storages);
		this.materials = new Material[256];
	}

	public void setMaterial(Material m) {
		for (int i = 0; i < materials.length; i++) {
			if (materials[i] != null) {
				continue;
			}
			materials[i] = m;
			materialCount++;
		}
	}

	@Override
	public void compile() {
		super.compile();

		this.diffuseTextures = MemoryUtil.memAllocInt(materialCount);
		this.diffuseColors = MemoryUtil.memAllocFloat(materialCount * 4);

		for (int i = 0; i < materialCount; i++) {
			Material m = materials[i];

			diffuseTextures.put(i);
			m.colorDiffuse.get(diffuseColors);
		}
		
		diffuseTextures.flip();
		diffuseColors.flip();
	}

	@Override
	public void render(int mode) {
		super.render(mode);
	}
}
