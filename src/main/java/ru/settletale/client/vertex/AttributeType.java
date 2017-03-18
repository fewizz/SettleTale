package ru.settletale.client.vertex;

import ru.settletale.util.PrimitiveType;

public enum AttributeType {
	FLOAT_4(new AttributeStorageFloat(4)),
	FLOAT_3(new AttributeStorageFloat(3)),
	FLOAT_2(new AttributeStorageFloat(2)),
	FLOAT_1(new AttributeStorageFloat(1)),
	INT_1(new AttributeStorageInt(1)),
	BYTE_4(new AttribyteStorageByte(4)),
	BYTE_4_NORMALISED(new AttribyteStorageByte(4), true),
	BYTE_3(new AttribyteStorageByte(3)),
	BYTE_1(new AttribyteStorageByte(1));

	final PrimitiveType primitiveType;
	//final Primitive attribPrimitiveType;
	final AttributeStorageAbstarct vs;
	final int perVertexElementCount;
	final boolean normalised;

	private AttributeType(AttributeStorageAbstarct vs) {
		this(vs, false);
	}
	
	//private AttributeType(AttributeStorageAbstarct vs, boolean normalised) {
	//	this(vs, normalised);
	//}*/
	
	/*private AttributeType(AttributeStorageAbstarct vs, Primitive attribPrimitiveType) {
		this(vs, attribPrimitiveType, false);
	}*/

	private AttributeType(AttributeStorageAbstarct vs, /*Primitive attribPrimitiveType, */boolean normalised) {
		this.vs = vs;
		this.perVertexElementCount = vs.count;
		this.primitiveType = vs.primitiveType;
		//this.attribPrimitiveType = attribPrimitiveType;
		this.normalised = normalised;
	}

	public AttributeStorageAbstarct getAttributeStorage() {
		return vs;
	}

	public int getElementCount() {
		return perVertexElementCount;
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}
	
	/*public Primitive getAttributePrimitiveType() {
		return attribPrimitiveType;
	}*/

	public boolean isNormalised() {
		return normalised;
	}
}