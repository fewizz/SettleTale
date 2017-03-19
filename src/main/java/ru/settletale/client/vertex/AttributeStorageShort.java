package ru.settletale.client.vertex;

import ru.settletale.util.PrimitiveType;

public class AttributeStorageShort extends AttributeStorageAbstarct {
	short s1;
	short s2;
	short s3;
	short s4;

	public AttributeStorageShort(int size) {
		super(size, PrimitiveType.SHORT);
	}

	@Override
	public void dataEnd(int id) {
		
	}

}
