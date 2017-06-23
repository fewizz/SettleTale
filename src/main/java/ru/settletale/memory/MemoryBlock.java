package ru.settletale.memory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public final class MemoryBlock {
	private static long allocatedTotal = 0;
	private long address = MemoryUtil.NULL;
	private int bytes = 0;

	public MemoryBlock() {
	}
	
	public static long getAllocatedTotal() {
		return allocatedTotal;
	}

	public MemoryBlock allocate(int bytes) {
		if(address != MemoryUtil.NULL) {
			throw new RuntimeException();
		}
		
		address = MemoryUtil.nmemAlloc(bytes);
		setBytes(bytes);
		return this;
	}

	public void reallocate(int bytes) {
		address = MemoryUtil.nmemRealloc(address, bytes);
		setBytes(bytes);
	}

	public void set(int value) {
		set(value, 0, bytes);
	}
	
	public void set(int value, int from, int bytes) {
		MemoryUtil.memSet(address + from, value, bytes);
	}
	
	public long address() {
		return address;
	}

	// Byte
	public byte getByte(int pos) {
		return MemoryUtil.memGetByte(address + pos);
	}

	public void putByte(int pos, byte value) {
		MemoryUtil.memPutByte(address + pos, value);
	}
	
	// Short
	public short getShort(int pos) {
		return MemoryUtil.memGetShort(address + pos);
	}

	public short getShortS(int pos) {
		return MemoryUtil.memGetShort(address + pos * Short.BYTES);
	}

	public void putShorS(int pos, short value) {
		MemoryUtil.memPutShort(address + pos * Short.BYTES, value);
	}

	// Float
	public float getFloat(int pos) {
		return MemoryUtil.memGetFloat(address + pos);
	}

	public float getFloatF(int pos) {
		return MemoryUtil.memGetFloat(address + pos * Float.BYTES);
	}

	public void putFloatF(int pos, float value) {
		MemoryUtil.memPutFloat(address + pos * Float.BYTES, value);
	}
	
	// Int
	public int getInt(int pos) {
		return MemoryUtil.memGetInt(address + pos);
	}

	public int getIntI(int pos) {
		return MemoryUtil.memGetInt(address + pos * Integer.BYTES);
	}

	public void putIntI(int pos, int value) {
		MemoryUtil.memPutInt(address + pos * Integer.BYTES, value);
	}
	
	// Double
	public double getDouble(int pos) {
		return MemoryUtil.memGetInt(address + pos);
	}

	public double getDoubleD(int pos) {
		return MemoryUtil.memGetInt(address + pos * Double.BYTES);
	}

	public void putDoubleD(int pos, double value) {
		MemoryUtil.memPutDouble(address + pos * Double.BYTES, value);
	}

	// Long
	public long getLong(int pos) {
		return MemoryUtil.memGetLong(address + pos);
	}

	public long getLongL(int pos) {
		return MemoryUtil.memGetLong(address + pos * Long.BYTES);
	}

	public void putLong(int pos, long value) {
		MemoryUtil.memPutLong(address + pos, value);
	}

	public void putLongL(int pos, long value) {
		MemoryUtil.memPutLong(address + pos * Long.BYTES, value);
	}

	public void free() {
		if(address == MemoryUtil.NULL) {
			throw new RuntimeException();
		}
		
		MemoryUtil.nmemFree(address);
		address = MemoryUtil.NULL;
		setBytes(0);
	}

	public void copyTo(long adrressDest, int src, int bytes) {
		MemoryUtil.memCopy(address + src, adrressDest, bytes);
	}
	
	public void copyTo(MemoryBlock destMemoryBlock, int src, int dest, int bytes) {
		copyTo(destMemoryBlock.address + dest, src, bytes);
	}

	public void copyTo(int src, int dest, int bytes) {
		copyTo(this, src, dest, bytes);
	}

	private void setBytes(int bytes) {
		allocatedTotal -= this.bytes;
		this.bytes = bytes;
		allocatedTotal += this.bytes;
	}

	public int bytes() {
		return bytes;
	}
	
	public int ints() {
		return bytes / Integer.BYTES;
	}

	public ByteBuffer getAsByteBuffer(int capacity) {
		return MemoryUtil.memByteBuffer(address, capacity);
	}

	public ByteBuffer getAsByteBuffer() {
		return getAsByteBuffer(bytes);
	}

	public FloatBuffer getAsFloatBuffer() {
		return MemoryUtil.memFloatBuffer(address, bytes / Float.BYTES);
	}
}
