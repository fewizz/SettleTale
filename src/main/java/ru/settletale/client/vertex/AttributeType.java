package ru.settletale.client.vertex;

import ru.settletale.util.PrimitiveType;

public enum AttributeType {
	UBYTE_1_INT_1(1, PrimitiveType.BYTE, PrimitiveType.INT),
	UBYTE_4_FLOAT_4_NORMALISED(4, PrimitiveType.UBYTE, PrimitiveType.FLOAT, true),
	
	INT_1(1, PrimitiveType.INT),
	
	FLOAT_4(4, PrimitiveType.FLOAT),
	FLOAT_3(3, PrimitiveType.FLOAT),
	FLOAT_2(2, PrimitiveType.FLOAT),
	FLOAT_1(1, PrimitiveType.FLOAT);

	final PrimitiveType primitiveType;
	final PrimitiveType attribPrimitiveType;
	final AttributeStorageAbstarct vs;
	final int perVertexElementCount;
	final boolean normalised;

	private AttributeType(int size, PrimitiveType dataPrimitiveType) {
		this(size, dataPrimitiveType, dataPrimitiveType, false);
	}
	
	private AttributeType(int size, PrimitiveType dataPrimitiveType, boolean normalised) {
		this(size, dataPrimitiveType, dataPrimitiveType, normalised);
	}
	
	private AttributeType(int size, PrimitiveType dataPrimitiveType, PrimitiveType attributePrimitiveType) {
		this(size, dataPrimitiveType, dataPrimitiveType, false);
	}
	
	private AttributeType(int size, PrimitiveType dataPrimitiveType, PrimitiveType attributePrimitiveType, boolean normalised) {
		switch (dataPrimitiveType) {
			case BYTE:
			case UBYTE:
				this.vs = new AttribyteStorageByte(size);
				break;
			case INT:
				this.vs = new AttributeStorageInt(size);
				break;
			case FLOAT:
				this.vs = new AttributeStorageFloat(size);
				break;
			default:
				this.vs = null;
		}
		
		this.perVertexElementCount = size;
		this.primitiveType = dataPrimitiveType;
		this.attribPrimitiveType = attributePrimitiveType;
		this.normalised = normalised;
	}

	public AttributeStorageAbstarct getAttributeStorage() {
		return vs;
	}

	public int getPerVertexElementCount() {
		return perVertexElementCount;
	}

	public PrimitiveType getDataType() {
		return primitiveType;
	}
	
	public PrimitiveType getAttributeDataType() {
		return attribPrimitiveType;
	}

	public boolean isNormalised() {
		return normalised;
	}
}