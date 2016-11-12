package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import ru.settletale.util.PrimitiveList;

public class PrimitiveArray {
	private Primitive.Type type;
	private PrimitiveList pl;
	private PrimitiveList cl;
	private PrimitiveList nl;
	private VertexInfo vert;
	private int lastVertex = 0;
	
	public PrimitiveArray(Primitive.Type type) {
		this.type = type;
		pl = new PrimitiveList(2048, Float.BYTES);
		cl = new PrimitiveList(2048, Byte.BYTES);
		nl = new PrimitiveList(2048, Float.BYTES);
		vert = new VertexInfo();
	}
	
	public void endVertex() {
		int start = lastVertex * 3;
		
		pl.putFloat(vert.pX, start + 0);
		pl.putFloat(vert.pY, start + 1);
		pl.putFloat(vert.pZ, start + 2);
		
		cl.put(vert.r, start + 0);
		cl.put(vert.g, start + 1);
		cl.put(vert.b, start + 2);
		
		nl.putFloat(vert.nX, start + 0);
		nl.putFloat(vert.nY, start + 1);
		nl.putFloat(vert.nZ, start + 2);
		
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
	
	public void color(byte r, byte g, byte b) {
		vert.r = r;
		vert.g = g;
		vert.b = b;
	}
	
	public void position(float x, float y, float z) {
		vert.pX = x;
		vert.pY = y;
		vert.pZ = z;
	}
	
	public void normal(float x, float y, float z) {
		vert.nX = x;
		vert.nY = y;
		vert.nZ = z;
	}
	
	public void clear() {
		pl.clear();
		cl.clear();
		nl.clear();
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
	
	public ByteBuffer getNormalBuffer() {
		nl.updateBuffer();
		return nl.buffer;
	}
	
	public int getVertexCount() {
		return this.lastVertex;
	}
	
	public int getPrimitiveCount() {
		return this.lastVertex / type.numOfVerts;
	}
}
