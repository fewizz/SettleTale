package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

public class PrimitiveArray {
	private VertexInfo vert;
	IVertexPositionStorage pos;
	IVertexColorStorage col;
	IVertexNormalStorage norm;
	private int lastVertex = 0;
	private int lastIndex = 0;

	public PrimitiveArray(Data... options) {
		for (Data data : options) {
			data.setBool(this);
		}

		vert = new VertexInfo();
	}

	public static enum Data {
		POSITION_3F {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.pos = new VertexPositionStorage3Float();
			}
		},
		NORMAL_3F {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.norm = new VertexNormalStorage3Float();
			}
		},
		COLOR_3B {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.col = new VertexColorStorage3Byte();
			}
		};

		void setBool(PrimitiveArray arr) {
		}
	}

	public void endVertex() {
		if (pos != null) {
			pos.position(vert.pX, vert.pY, vert.pZ, lastVertex);
		}
		if (norm != null) {
			norm.normal(vert.nX, vert.nY, vert.nZ, lastVertex);
		}
		if (col != null) {
			col.color(vert.r, vert.g, vert.b, vert.a, lastVertex);
		}

		lastVertex++;
	}
	
	public void index(int index) {
		if (pos != null) {
			pos.index(index, lastIndex);
		}
		if (norm != null) {
			pos.index(index, lastIndex);
		}
		if (col != null) {
			pos.index(index, lastIndex);
		}

		lastIndex++;
	}

	public void vertexInfo(VertexInfo vert) {
		this.vert = vert;
	}

	public void clear() {
		if (pos != null)
			pos.clear();
		if (col != null)
			col.clear();
		if (norm != null)
			norm.clear();
		lastVertex = 0;
		lastIndex = 0;
	}

	public ByteBuffer getPositionBuffer() {
		return pos.getBuffer();
	}

	public ByteBuffer getColorBuffer() {
		return col.getBuffer();
	}

	public ByteBuffer getNormalBuffer() {
		return norm.getBuffer();
	}

	public ByteBuffer getPositionIndexBuffer() {
		return pos.getIndexBuffer();
	}

	public ByteBuffer getColorIndexBuffer() {
		return col.getIndexBuffer();
	}

	public ByteBuffer getNormalIndexBuffer() {
		return norm.getIndexBuffer();
	}

	public int getVertexCountIndexed() {
		return this.lastVertex;
	}

	public int getVertexCount() {
		return this.lastVertex;
	}

	public int getIndexCount() {
		return this.lastIndex;
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
	
	public void color(byte r, byte g, byte b, byte a) {
		vert.r = r;
		vert.g = g;
		vert.b = b;
		vert.a = a;
	}
}
