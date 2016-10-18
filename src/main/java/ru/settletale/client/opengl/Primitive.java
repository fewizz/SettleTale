package ru.settletale.client.opengl;

public class Primitive {
	Type type;
	byte lastVert = 0;
	VertexInfo[] veticies;
	
	public Primitive(Type type) {
		this.type = type;
		veticies = new VertexInfo[type.numOfVerts];
	}
	
	public void vertex(VertexInfo vert) {
		veticies[lastVert] = vert;
		lastVert++;
	}
	
	public enum Type {
		Triangle(3),
		Quad(4);
		
		Type(int numOfVerts) {
			this.numOfVerts = numOfVerts;
		}
		
		int numOfVerts;
	}
}
