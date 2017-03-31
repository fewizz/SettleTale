package ru.settletale.client.vertex;

import ru.settletale.util.PrimitiveType;

public enum VertexAttribType {
	UBYTE_1_INT_1(1, PrimitiveType.BYTE, PrimitiveType.INT),
	UBYTE_4_FLOAT_4_NORMALISED(4, PrimitiveType.UBYTE, PrimitiveType.FLOAT, true),
	
	INT_1(1, PrimitiveType.INT),
	
	FLOAT_4(4, PrimitiveType.FLOAT),
	FLOAT_3(3, PrimitiveType.FLOAT),
	FLOAT_2(2, PrimitiveType.FLOAT),
	FLOAT_1(1, PrimitiveType.FLOAT);

	final PrimitiveType clientDataType;
	final PrimitiveType serverDataType;
	final int perVertexElementCount;
	final boolean normalised;

	private VertexAttribType(int size, PrimitiveType dataType) {
		this(size, dataType, dataType, false);
	}
	
	private VertexAttribType(int size, PrimitiveType dataType, boolean normalised) {
		this(size, dataType, dataType, normalised);
	}
	
	private VertexAttribType(int size, PrimitiveType dataType, PrimitiveType attributeType) {
		this(size, dataType, dataType, false);
	}
	
	private VertexAttribType(int size, PrimitiveType dataType, PrimitiveType attributeType, boolean normalised) {
		this.perVertexElementCount = size;
		this.clientDataType = dataType;
		this.serverDataType = attributeType;
		this.normalised = normalised;
	}
	
	public AttribArrayData getNewVertexAttribArrayBuffer() {
		switch (clientDataType) {
			case BYTE:
			case UBYTE:
				return new AttribArrayDataByte(perVertexElementCount, this);
			case INT:
				return new AttribArrayDataInt(perVertexElementCount, this);
			case FLOAT:
				return new AttribArrayDataFloat(perVertexElementCount, this);
			default:
				return null;
		}
	}

	public int getPerVertexElementCount() {
		return perVertexElementCount;
	}

	public PrimitiveType getClientDataType() {
		return clientDataType;
	}
	
	public PrimitiveType getServerDataType() {
		return serverDataType;
	}

	public boolean isNormalised() {
		return normalised;
	}
}