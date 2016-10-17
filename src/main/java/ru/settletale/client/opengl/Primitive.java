package ru.settletale.client.opengl;

public class Primitive {
	Type type;
	byte lastVert = 0;
	Vertex[] veticies;
	
	public Primitive(Type type) {
		this.type = type;
		veticies = new Vertex[type.numOfVerts];
	}
	
	public void vertex(Vertex vert) {
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
