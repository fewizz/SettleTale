package ru.settletale.client.vertex;

public class VertexInfo {
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
	public byte a;
	
	public void color(byte r, byte g, byte b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void position(float x, float y, float z) {
		this.pX = x;
		this.pY = y;
		this.pZ = z;
	}
	
	public void normal(float x, float y, float z) {
		this.nX = x;
		this.nY = y;
		this.nZ = z;
	}
}
