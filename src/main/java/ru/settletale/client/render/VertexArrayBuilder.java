package ru.settletale.client.render;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.gl.VertexArray;
import ru.settletale.memory.MemoryBlock;

public class VertexArrayBuilder {
	public final int TEMP_MEMORY_BLOCK_BYTES = Double.BYTES * 4;
	final HashIntObjMap<VertexAttribArray> attribArrays;
	final HashIntObjMap<MemoryBlock> memoyBlocks;
	int vertex = 0;
	
	public VertexArrayBuilder(int vertexNumber, Attrib... attribs) {
		this.attribArrays = HashIntObjMaps.newMutableMap();
		this.memoyBlocks = HashIntObjMaps.newMutableMap();
		
		for(int i = 0; i < attribs.length; i++) {
			Attrib attrib = attribs[i];
			
			if(attribArrays.containsKey(attrib.location))
				throw new Error("Location " + attrib.location + " uses multiple times");
			
			VertexAttribArray attribArray = new VertexAttribArray(attrib, vertexNumber);
			this.attribArrays.put(attrib.location, attribArray);
			
			MemoryBlock mb = new MemoryBlock().allocate(TEMP_MEMORY_BLOCK_BYTES);
			this.memoyBlocks.put(attrib.location, mb);
		}
		
		Renderer.debugGL("vao builder init");
	}
	
	public void endVertex() {
		memoyBlocks.forEach((int location, MemoryBlock mb) -> {
			VertexAttribArray vaa = attribArrays.get(location);
			int step = vaa.attribInfo.componentsBytes;
			mb.copyTo(vaa, 0, step * vertex, step);
		});
		
		vertex++;
	}
	
	public VertexArray build() {
		return this.build(new VertexArray().gen());
	}
	
	public VertexArray build(VertexArray vao) {
		Renderer.debugGL("pre vao build");
		attribArrays.forEach((int location, VertexAttribArray attribArray) -> {
			memoyBlocks.get(location).free();
			attribArray.updateServerData();
			attribArray.freeClientData();
			vao.vertexAttribPointer(attribArray);
		});
		
		Renderer.debugGL("post vao build");
		return vao;
	}
	
	public VertexArrayBuilder float3(int location, float f1, float f2, float f3) {
		MemoryBlock mb = memoyBlocks.get(location);
		//VertexAttribArray vaa = attribArrays.get(location);
		
		//int step = vaa.attribInfo.componentsBytes;
		//int start = vertex * step;
		
		mb.putFloatF(0, f1);
		mb.putFloatF(1, f2);
		mb.putFloatF(2, f3);
		return this;
	}
	
	public VertexArrayBuilder float1(int location, float f) {
		MemoryBlock mb = memoyBlocks.get(location);
		//VertexAttribArray vaa = attribArrays.get(location);
		
		//int step = vaa.attribInfo.componentsBytes;
		//int start = vertex * step;
		
		mb.putFloatF(1, f);
		return this;
	}
}
