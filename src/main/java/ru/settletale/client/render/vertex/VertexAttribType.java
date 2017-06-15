package ru.settletale.client.render.vertex;

import ru.settletale.util.PrimitiveType;

public enum VertexAttribType {
	UBYTE_1_INT_1(1, PrimitiveType.UBYTE, PrimitiveType.INT),
	UBYTE_4_FLOAT_4_NORMALISED(4, PrimitiveType.UBYTE, PrimitiveType.FLOAT, true),
	
	INT_1(1, PrimitiveType.INT),
	
	FLOAT_4(4, PrimitiveType.FLOAT),
	FLOAT_3(3, PrimitiveType.FLOAT),
	FLOAT_2(2, PrimitiveType.FLOAT),
	FLOAT_1(1, PrimitiveType.FLOAT);

	final PrimitiveType clientDataType;
	final PrimitiveType serverDataType;
	final int componentCount;
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
		this.componentCount = size;
		this.clientDataType = dataType;
		this.serverDataType = attributeType;
		this.normalised = normalised;
	}

	public int getPerVertexElementCount() {
		return componentCount;
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