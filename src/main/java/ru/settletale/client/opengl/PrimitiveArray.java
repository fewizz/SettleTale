package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import ru.settletale.util.FloatPrimitiveList;

public class PrimitiveArray {
	Primitive.Type type;
	FloatPrimitiveList pl;
	FloatPrimitiveList cl;
	VertexInfo vert;
	private int lastVertex = 0;
	
	public PrimitiveArray(Primitive.Type type) {
		this.type = type;
		pl = new FloatPrimitiveList(2048, Float.BYTES);
		cl = new FloatPrimitiveList(2048, Byte.BYTES);
	}
	
	public void endVertex() {
		int start = lastVertex * 3;
		
		pl.putFloat(vert.pX, start + 0);
		pl.putFloat(vert.pY, start + 1);
		pl.putFloat(vert.pZ, start + 2);
		
		cl.put(vert.r, start + 0);
		cl.put(vert.g, start + 1);
		cl.put(vert.b, start + 2);
		
		lastVertex++;
	}
	
	public void vertex(VertexInfo vert) {
		this.vert = vert;
	}
	
	public void positionAndColor(float x, float y, float z, byte r, byte g, byte b) {
		vert.pX = x;
		vert.pY = y;
		vert.pZ = z;
		vert.r = r;
		vert.g = g;
		vert.b = b;
	}
	
	public void clear() {
		pl.clear();
		cl.clear();
		lastVertex = 0;
	}
	
	public ByteBuffer getPositionBuffer() {
		pl.updateBuffer();
		return pl.buffer;
	}
	
	public ByteBuffer getColorBuffer() {
		cl.updateBuffer();
		return cl.buffer;
	}
	
	public int getVertexCount() {
		return this.lastVertex;
	}
	
	public int getPrimitiveCount() {
		return this.lastVertex / type.numOfVerts;
	}
}
