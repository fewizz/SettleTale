package ru.settletale.client.opengl;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class PrimitiveArray {
	Primitive.Type type;
	List<Primitive> list;
	int lastPrim = 0;
	
	public PrimitiveArray(Primitive.Type type) {
		this.type = type;
		list = new ArrayList<>();
	}
	
	public void vertex(Vertex vert) {
		if(list.size() <= lastPrim) {
			list.add(new Primitive(type));
		}
		Primitive prim = list.get(lastPrim);
		if(prim == null) {
			list.add(lastPrim, new Primitive(type));
		}
		
		prim.vertex(vert);
		
		if(prim.lastVert >= type.numOfVerts) {
			lastPrim++;
		}
	}
	
	public FloatBuffer getPositionBuffer() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(list.size() * type.numOfVerts * 3);
		
		for(Primitive p : list) {
			for(int i = 0; i < type.numOfVerts; i++) {
				fb.put((float) p.veticies[i].pX);
				fb.put((float) p.veticies[i].pY);
				fb.put((float) p.veticies[i].pZ);
			}
		}
		
		fb.flip();
		return fb;
	}
	
	public FloatBuffer getColorBuffer() {
		FloatBuffer fb = BufferUtils.createFloatBuffer(list.size() * type.numOfVerts * 3);
		
		for(Primitive p : list) {
			for(int i = 0; i < type.numOfVerts; i++) {
				fb.put(((float)(p.veticies[i].r & 0xFF)) / 255F);
				fb.put(((float)(p.veticies[i].g & 0xFF)) / 255F);
				fb.put(((float)(p.veticies[i].b & 0xFF)) / 255F);
			}
		}
		
		fb.flip();
		return fb;
	}
}
