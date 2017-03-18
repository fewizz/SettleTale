package ru.settletale.util;

public enum Primitive {
	FLOAT(Float.BYTES, false),
	INT(Integer.BYTES, true),
	BYTE(Byte.BYTES, true),
	DOUBLE(Double.BYTES, false),
	SHORT(Short.BYTES, true),
	CHAR(Character.BYTES, true),
	LONG(Long.BYTES, true);
	
	private Primitive(int sizeInBytes, boolean isInt) {
		this.sizeInBytes = sizeInBytes;
		this.isInt = isInt;
	}
	
	public boolean isInt;
	public int sizeInBytes;
}
