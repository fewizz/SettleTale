package ru.settletale.client.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.vertex.VertexAttribType;

public class RenderLayerMaterialised extends RenderLayer {
	List<Material> materials;
	IntBuffer diffuseTextures;
	FloatBuffer diffuseColors;
	int materialAttribIndex = -1;

	public RenderLayerMaterialised(VertexAttribType... storages) {
		super(storages);
		this.materials = new ArrayList<>();
	}

	public void setMaterialAttribIndex(int index) {
		this.materialAttribIndex = index;
	}

	public void setMaterial(Material m) {
		if(materialAttribIndex == -1) {
			throw new Error("Material attribute index not set");
		}
		
		if(!materials.contains(m)) {
			materials.add(m);
		}
		
		int matID = materials.indexOf(m);
		getVertexArrayDataBaker().putByte(matID, (byte)matID);
	}

	@Override
	public void compile() {
		super.compile();

		this.diffuseTextures = MemoryUtil.memAllocInt(materials.size());
		this.diffuseColors = MemoryUtil.memAllocFloat(materials.size() * 4);

		diffuseTextures.flip();
		diffuseColors.flip();
	}

	@Override
	public void render(int mode) {
		super.render(mode);
	}
}
