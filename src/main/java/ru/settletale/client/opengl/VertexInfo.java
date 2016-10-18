package ru.settletale.client.opengl;

public class VertexInfo {
	public static final int SIZE = Double.BYTES * 3 + Float.BYTES * 5 + Byte.BYTES * 3;
	
	public double pX;
	public double pY;
	public double pZ;
	public float tX;
	public float tY;
	public float nX;
	public float nY;
	public float nZ;
	public byte r;
	public byte g;
	public byte b;
	
}
