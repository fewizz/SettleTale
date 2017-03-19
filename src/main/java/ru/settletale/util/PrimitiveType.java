package ru.settletale.util;

public enum PrimitiveType {
	FLOAT(Float.BYTES, false),
	INT(Integer.BYTES, true),
	BYTE(Byte.BYTES, true),
	UBYTE(Byte.BYTES, true),
	DOUBLE(Double.BYTES, false),
	SHORT(Short.BYTES, true),
	CHAR(Character.BYTES, true),
	LONG(Long.BYTES, true);
	
	private PrimitiveType(int sizeInBytes, boolean isIntegral) {
		this.sizeInBytes = sizeInBytes;
		this.isIntegral = isIntegral;
	}
	
	public boolean isIntegral() {
		return isIntegral;
	}
	
	public int getSizeInBytes() {
		return sizeInBytes;
	}
	
	boolean isIntegral;
	int sizeInBytes;
}
