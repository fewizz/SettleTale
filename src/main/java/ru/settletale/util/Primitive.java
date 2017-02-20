package ru.settletale.util;

public enum Primitive {
	FLOAT(Float.BYTES),
	INT(Integer.BYTES),
	BYTE(Byte.BYTES),
	DOUBLE(Double.BYTES),
	SHORT(Short.BYTES),
	CHAR(Character.BYTES),
	LONG(Long.BYTES);
	
	private Primitive(int sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
	public int sizeInBytes;
}
