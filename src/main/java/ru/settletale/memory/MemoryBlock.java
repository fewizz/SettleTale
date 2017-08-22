package ru.settletale.memory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class MemoryBlock {
	static final boolean DEBUG = true;
	private static long allocatedTotal = 0;
	StackTraceElement[] allocStackTrace;
	protected long address = MemoryUtil.NULL;
	protected int bytes = 0;

	public MemoryBlock() {
	}
	
	public MemoryBlock(int bytes) {
		allocate(bytes);
	}
	
	@Override
	protected void finalize() throws Throwable {
		if(allocStackTrace != null) {
			System.err.println("Memory block allocated is not free'ed:");
			for(StackTraceElement s : allocStackTrace) {
				System.err.println(s);
			}
		}
	}
	
	public static long getAllocatedTotal() {
		return allocatedTotal;
	}

	public MemoryBlock allocate(int bytes) {
		if(address != MemoryUtil.NULL) {
			throw new RuntimeException();
		}
		
		if(DEBUG) {
			allocStackTrace = Thread.currentThread().getStackTrace();
		}
		
		address = MemoryUtil.nmemAlloc(bytes);
		setBytes(bytes);
		return this;
	}
	
	public MemoryBlock allocateS(int shorts) {
		return allocate(shorts * Short.BYTES);
	}
	
	public MemoryBlock allocateI(int ints) {
		return allocate(ints * Integer.BYTES);
	}

	public void reallocate(int bytes) {
		if(DEBUG) {
			allocStackTrace = Thread.currentThread().getStackTrace();
		}
		
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
	public byte get(int pos) {
		return MemoryUtil.memGetByte(address + pos);
	}

	public MemoryBlock put(int pos, byte value) {
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutByte(address + pos, value);
		return this;
	}
	
	// Short
	public short getShort(int pos) {
		return MemoryUtil.memGetShort(address + pos);
	}

	public short getShortS(int pos) {
		return MemoryUtil.memGetShort(address + pos * Short.BYTES);
	}

	public void putShortS(int pos, short value) {
		pos *= Short.BYTES;
		if(DEBUG) if(pos  < 0 || pos  >= address) throw new Error();
		MemoryUtil.memPutShort(address + pos, value);
	}

	// Float
	public float getFloat(int pos) {
		return MemoryUtil.memGetFloat(address + pos);
	}

	public float getFloatF(int pos) {
		return MemoryUtil.memGetFloat(address + pos * Float.BYTES);
	}

	public MemoryBlock putFloatF(int pos, float value) {
		pos *= Float.BYTES;
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutFloat(address + pos, value);
		return this;
	}
	
	/*public MemoryBlock putFloatF(float value) {
		return putFloatF(position, value).position(position + Float.BYTES);
	}*/
	
	// Int
	public int getInt(int pos) {
		return MemoryUtil.memGetInt(address + pos);
	}

	public int getIntI(int pos) {
		return MemoryUtil.memGetInt(address + pos * Integer.BYTES);
	}
	
	public void putInt(int pos, int value) {
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutInt(address + pos, value);
	}

	public void putIntI(int pos, int value) {
		pos *= Integer.BYTES;
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutInt(address + pos, value);
	}
	
	// Double
	public double getDouble(int pos) {
		return MemoryUtil.memGetDouble(address + pos);
	}

	public double getDoubleD(int pos) {
		return MemoryUtil.memGetDouble(address + pos * Double.BYTES);
	}

	public void putDoubleD(int pos, double value) {
		pos *= Double.BYTES;
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutDouble(address + pos, value);
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
		pos *= Long.BYTES;
		if(DEBUG) if(pos < 0 || pos >= address) throw new Error();
		MemoryUtil.memPutLong(address + pos, value);
	}

	public void free() {
		if(address == MemoryUtil.NULL) {
			throw new RuntimeException();
		}
		
		allocStackTrace = null;
		
		
		MemoryUtil.nmemFree(address);
		address = MemoryUtil.NULL;
		setBytes(0);
		//limit(0);
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
		//limit(bytes);
		//position(0);
		allocatedTotal += this.bytes;
	}

	public int bytes() {
		return bytes;
	}
	
	public int ints() {
		return bytes / Integer.BYTES;
	}
	
	/*public int limit() {
		return this.limit;
	}
	
	public MemoryBlock limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public int position() {
		return this.position;
	}
	
	public MemoryBlock position(int pos) {
		this.position = pos;
		return this;
	}
	
	public int remaining() {
		return limit - position;
	}*/

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
