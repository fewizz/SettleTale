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
		this.bytes = sizeInBytes;
		this.isIntegral = isIntegral;
	}
	
	public final boolean isIntegral;
	public final int bytes;
}
