package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import ru.settletale.util.FloatPrimitiveList;

public class PrimitiveArray {
	Primitive.Type type;
	//List<Primitive> list;
	int lastPrim = 0;
	FloatPrimitiveList pl;
	ByteBuffer pb;
	FloatPrimitiveList cl;
	ByteBuffer cb;
	
	public PrimitiveArray(Primitive.Type type) {
		this.type = type;
		//list = new ArrayList<>();
		pl = new FloatPrimitiveList(64, Float.BYTES);
		pb = BufferUtils.createByteBuffer(1);
		cl = new FloatPrimitiveList(64, Float.BYTES);
		cb = BufferUtils.createByteBuffer(1);
	}
	
	public void vertex(VertexInfo vert) {
		int start = lastPrim * 3;
		pl.put((float) vert.pX, start + 0);
		pl.put((float) vert.pY, start + 1);
		pl.put((float) vert.pZ, start + 2);
		
		cl.put((float) (vert.r & 0xFF) / 255F, start + 0);
		cl.put((float) (vert.g & 0xFF) / 255F, start + 1);
		cl.put((float) (vert.b & 0xFF) / 255F, start + 2);
		
		lastPrim++;
		/*if(list.size() <= lastPrim) {
			list.add(new Primitive(type));
		}
		Primitive prim = list.get(lastPrim);
		if(prim == null) {
			list.add(lastPrim, new Primitive(type));
		}
		
		prim.vertex(vert);
		
		if(prim.lastVert >= type.numOfVerts) {
			lastPrim++;
		}*/
	}
	
	public void vertex(float x, float y, float z, byte r, byte g, byte b) {
		int start = lastPrim * 3;
		pl.put((float) x, start + 0);
		pl.put((float) x, start + 1);
		pl.put((float) x, start + 2);
		
		cl.put((float) (r & 0xFF) / 255F, start + 0);
		cl.put((float) (g & 0xFF) / 255F, start + 1);
		cl.put((float) (b & 0xFF) / 255F, start + 2);
		
		lastPrim++;
	}
	
	public void clear() {
		pl.clear();
		cl.clear();
		lastPrim = 0;
	}
	
	public ByteBuffer getPositionBuffer() {
		/*FloatBuffer fb = BufferUtils.createFloatBuffer(list.size() * type.numOfVerts * 3);
		
		for(Primitive p : list) {
			for(int i = 0; i < type.numOfVerts; i++) {
				fb.put((float) p.veticies[i].pX);
				fb.put((float) p.veticies[i].pY);
				fb.put((float) p.veticies[i].pZ);
			}
		}
		
		fb.flip();
		return fb;*/
		pl.toBuffer(pb);
		return pb;
	}
	
	public ByteBuffer getColorBuffer() {
		/*FloatBuffer fb = BufferUtils.createFloatBuffer(list.size() * type.numOfVerts * 3);
		
		for(Primitive p : list) {
			for(int i = 0; i < type.numOfVerts; i++) {
				fb.put(((float)(p.veticies[i].r & 0xFF)) / 255F);
				fb.put(((float)(p.veticies[i].g & 0xFF)) / 255F);
				fb.put(((float)(p.veticies[i].b & 0xFF)) / 255F);
			}
		}
		
		fb.flip();
		return fb;*/
		cl.toBuffer(cb);
		return cb;
	}
}
