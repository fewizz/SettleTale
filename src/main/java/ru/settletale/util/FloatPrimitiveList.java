package ru.settletale.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sun.misc.Unsafe;

public class FloatPrimitiveList {
	private final int pageSize;
	private final int pageSizeBytes;
	private final int pageAddrSize;
	private final int pageAddrBytes;
	private final int pageAddrBytesFull;
	private final int bytes;
	
	public int length = 0;
	private long firstAddrPage;
	
	private long lastAddr;
	private int lastAddrInd;
	
	public ByteBuffer buffer;
	private long bufferAddress;
	private long bufferFOffset;
	
	public static void main(String[] args) {
		FloatPrimitiveList lst = new FloatPrimitiveList(1, 1);
		
		for(int i = 0; i < 64; i++) {
			lst.put((byte)i, i);
		}
		
		lst.updateBuffer();
		
		for(int i = 0; i < lst.length; i++) {
			System.out.println(lst.buffer.get(i));
		}
	}

	public FloatPrimitiveList(int pageSize, int typeBytes) {
		this.pageSize = pageSize;
		this.bytes = typeBytes;
		this.pageSizeBytes = this.bytes * pageSize;
		this.pageAddrSize = 64;
		this.pageAddrBytes = this.pageAddrSize * Long.BYTES;
		this.pageAddrBytesFull = pageAddrBytes + Long.BYTES;
		this.firstAddrPage = createNewAddrPage();
		lastAddr = firstAddrPage;
		lastAddrInd = 0;
		this.buffer = ByteBuffer.allocateDirect(1);
		this.buffer.order(ByteOrder.nativeOrder());
		this.bufferFOffset = u.objectFieldOffset(ByteBufferUtils.addressField);
		this.bufferAddress = u.getLong(buffer, bufferFOffset);
		ByteBufferUtils.updateDealloc(buffer, bufferAddress, 1);
	}

	public void put(byte value, int index) {
		u.putByte(getValueAdress(index, true), value);
		setLen(index);
	}
	
	public void putFloat(float value, int index) {
		u.putFloat(getValueAdress(index, true), value);
		setLen(index);
	}
	
	public void putInt(int value, int index) {
		u.putInt(getValueAdress(index, true), value);
		setLen(index);
	}
	
	public byte get(int index) {
		return u.getByte(getValueAdress(index, false));
	}
	
	public float getFloat(int index) {
		return u.getFloat(getValueAdress(index, false));
	}
	
	private void setLen(int index) {
		length = Math.max(length, index + 1);
	}
	
	private long getValueAdress(int index, boolean createNew) {
		int pageAddrIndex = index / pageSize / pageAddrSize;
		
		long addrToNext = -1;
		
		if(pageAddrIndex == lastAddrInd) {
			addrToNext = lastAddr;
		}
		else {
			addrToNext = firstAddrPage;
			
			for (int i = 0; i < pageAddrIndex; i++) {
				long a = u.getAddress(addrToNext + pageAddrBytes);
				if (a == -1) {
					if(!createNew) {
						return 0;
					}
					a = createNewAddrPage();
					u.putAddress(addrToNext + pageAddrBytes, a);
				}
				addrToNext = a;
			}
		}
		
		lastAddr = addrToNext;
		lastAddrInd = pageAddrIndex;

		long addr = addrToNext + ((index / pageSize) % pageAddrSize) * Long.BYTES;
		long addrToPage = u.getAddress(addr);

		if (addrToPage == -1) {
			if(!createNew) {
				return 0;
			}
			addrToPage = createNewPage();
			u.putLong(addr, addrToPage);
		}
		return addrToPage + ((index % pageSize) * bytes);
	}

	private long createNewPage() {
		long l = u.allocateMemory(pageSizeBytes);
		for (int i = 0; i < pageSizeBytes; i += Long.BYTES) {
			if(pageSizeBytes - i < Long.BYTES) { // If page power not eq long bytes count
				for(int z = 0; z < pageSizeBytes - i; z++) {
					u.putByte(l + i + z, (byte) 0);
				}
				break;
			}
			
			u.putLong(l + i, 0);
		}
		return l;
	}

	private long createNewAddrPage() {
		long newAddPage = u.allocateMemory(pageAddrBytesFull);
		for (int i = 0; i < pageAddrSize; i++) {
			u.putLong(newAddPage + (i * Long.BYTES), -1);
		}
		u.putLong(newAddPage + (pageAddrSize * Long.BYTES), -1); // For next addr page
		return newAddPage;
	}
	
	public void clear() {
		long addrToNext = firstAddrPage;
		
		for (int i = 0; i < length / pageSize / pageAddrSize; i++) {
			if (addrToNext == -1) {
				continue;
			}
			
			for(int x = 0; x < pageAddrSize; x++) {
				long a = u.getAddress(addrToNext + (x * Long.BYTES));
				
				if(a == -1) {
					continue;
				}
				
				u.freeMemory(a);
			}
			long prev = addrToNext;
			addrToNext = u.getAddress(addrToNext + pageAddrBytes);
			u.freeMemory(prev);
		}
		
		firstAddrPage = createNewAddrPage();
		lastAddr = firstAddrPage;
		lastAddrInd = 0;
		length = 0;
	}

	public void updateBuffer() {
		if(buffer.capacity() < length * bytes || buffer.capacity() > length * bytes) {
			try {
				Field cap = Buffer.class.getDeclaredField("capacity");
				cap.setAccessible(true);
				cap.setInt(buffer, length * bytes);
				u.freeMemory(bufferAddress);
				bufferAddress = u.allocateMemory(length * bytes);
				u.putLong(buffer, bufferFOffset, bufferAddress);
				buffer.limit(length * bytes);
				buffer.position(0);
				ByteBufferUtils.updateDealloc(buffer, bufferAddress, length * bytes);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		int index = 0;

		long addrToNext = firstAddrPage;

		int si = SSMath.ceil((float) length / (float) pageAddrSize / (float) pageSize);
		int lenBytes = length * bytes;

		for (int i = 0; i < si; i++) {
			if(addrToNext == -1) {
				return;
			}
			
			for (int x = 0; x < pageAddrSize; x++) {
				long addr = addrToNext + (x * Long.BYTES);
				long addrToPage = u.getAddress(addr);
				if (lenBytes - index <= 0) {
					return;
				}
				if (lenBytes - index < pageSizeBytes) {
					u.copyMemory(addrToPage, bufferAddress + index, lenBytes - index);
					return;
				}
				u.copyMemory(addrToPage, bufferAddress + index, pageSizeBytes);
				index += pageSizeBytes;
			}

			addrToNext = u.getAddress(addrToNext + pageAddrBytes);
		}
	}

	/** Unsafe stuff **/
	public static Unsafe u;

	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");

			f.setAccessible(true);

			u = (Unsafe) f.get(null);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} 

	}
}
