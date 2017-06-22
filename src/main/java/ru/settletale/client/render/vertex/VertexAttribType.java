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

	public final PrimitiveType clientDataType;
	public final PrimitiveType serverDataType;
	public final int componentCount;
	public final boolean isNormalised;

	VertexAttribType(int components, PrimitiveType dataType) {
		this(components, dataType, dataType, false);
	}
	
	VertexAttribType(int components, PrimitiveType dataType, boolean isNormalised) {
		this(components, dataType, dataType, isNormalised);
	}
	
	VertexAttribType(int components, PrimitiveType clientDataType, PrimitiveType serverDataType) {
		this(components, clientDataType, serverDataType, false);
	}
	
	VertexAttribType(int components, PrimitiveType clientDataType, PrimitiveType serverDataType, boolean isNormalised) {
		this.componentCount = components;
		this.clientDataType = clientDataType;
		this.serverDataType = serverDataType;
		this.isNormalised = isNormalised;
	}
	
	static final VertexAttribType[] VALUES = values();
	
	public static VertexAttribType get(int components, PrimitiveType clientDataType, PrimitiveType attributeType, boolean isNormalised) {
		for(VertexAttribType attr : VALUES) {
			if(attr.isNormalised == isNormalised && attr.componentCount == components && attr.clientDataType == clientDataType && attr.serverDataType == attributeType) {
				return attr;
			}
		}
		
		return null;
	}
}