package ru.settletale.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

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

	/*public static void main(String[] args) {
		//System.out.println(Math.ceil(0.5F));
		FloatPrimitiveList lst = new FloatPrimitiveList(3, Float.BYTES);
		ByteBuffer b = ByteBuffer.allocateDirect(1);
		b.order(ByteOrder.nativeOrder());
		
		for(int i = 0; i < 5; i++) {
			System.out.println(i);
			lst.put(1F, i);
		}
		
		lst.toBuffer(b);
		
		FloatBuffer fb = b.asFloatBuffer();

		for (int i = 0; i < fb.capacity(); i++) {
			System.out.println(fb.get(i) + " :" + i);
		}
		
		lst.clear();
		
		for(int i = 0; i < 5; i++) {
			System.out.println(i);
			lst.put(1F, i);
		}
		
		lst.toBuffer(b);
		fb = b.asFloatBuffer();
		
		for (int i = 0; i < fb.capacity(); i++) {
			System.out.println(fb.get(i) + " :" + i);
		}
		
		lst.clear();
	}*/

	public FloatPrimitiveList(int pageSize, int typeBytes) {
		this.pageSize = pageSize;
		this.bytes = typeBytes;
		this.pageSizeBytes = this.bytes * pageSize;
		this.pageAddrSize = 64;
		this.pageAddrBytes = this.pageAddrSize * Long.BYTES;
		this.pageAddrBytesFull = pageAddrBytes + Long.BYTES;
		this.firstAddrPage = createNewAddrPage();
	}

	public void put(float value, int index) {
		u.putFloat(getPutAdress(index), value);
		setLen(index);
	}
	
	public void put(byte value, int index) {
		u.putByte(getPutAdress(index), value);
		setLen(index);
	}
	
	public void put(int value, int index) {
		u.putInt(getPutAdress(index), value);
		setLen(index);
	}
	
	private void setLen(int index) {
		length = Math.max(length, index + 1);
	}
	
	private long getPutAdress(int index) {
		int pageAddrIndex = index / pageSize / pageAddrSize;

		long addrToNext = firstAddrPage;
		for (int i = 0; i < pageAddrIndex; i++) {
			long a = u.getAddress(addrToNext + pageAddrBytes);
			if (a == -1) {
				a = createNewAddrPage();
				u.putAddress(addrToNext + pageAddrBytes, a);
			}
			addrToNext = a;
		}

		long addr = addrToNext + ((index / pageSize) % pageAddrSize) * Long.BYTES;
		long addrToPage = u.getAddress(addr);

		if (addrToPage == -1) {
			addrToPage = createNewPage();
			u.putLong(addr, addrToPage);
		}
		return addrToPage + ((index % pageSize) * bytes);
	}

	public float get(int index) {
		int pageAddrIndex = index / pageSize / pageAddrSize;

		long addrToNext = firstAddrPage;
		for (int i = 0; i < pageAddrIndex; i++) {
			long a = u.getAddress(addrToNext + pageAddrBytes);
			if (a == -1) {
				return 0;
			}
			addrToNext = a;
		}

		long addr = addrToNext + ((index / pageSize) % pageAddrSize) * Long.BYTES;
		long addrToPage = u.getAddress(addr);

		if (addrToPage == -1) {
			return 0;
		}

		return u.getFloat(addrToPage + ((index % pageSize) * bytes));
	}

	private long createNewPage() {
		long l = u.allocateMemory(pageSizeBytes);
		for (int i = 0; i < pageSize; i++) {
			u.putInt(l + (bytes * i), 0);
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
		length = 0;
	}

	public ByteBuffer toBuffer(ByteBuffer buffer) {
		Field f_add = null;
		long bufferAddress = 0;
		
		try {
			f_add = Buffer.class.getDeclaredField("address");
			f_add.setAccessible(true);
			bufferAddress = f_add.getLong(buffer);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(buffer.capacity() < length * bytes || buffer.capacity() > length * bytes) {
			try {
				Field cap = Buffer.class.getDeclaredField("capacity");
				cap.setAccessible(true);
				cap.setInt(buffer, length * bytes);
				u.freeMemory(bufferAddress);
				bufferAddress = u.allocateMemory(length * bytes);
				f_add.setLong(buffer, bufferAddress);
				buffer.limit(length * bytes);
				buffer.position(0);
				ByteBufferUtils.setDealloc(buffer, bufferAddress, length * bytes);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		int index = 0;

		long addrToNext = firstAddrPage;

		//float s = (float) length / (float) pageAddrSize / (float) pageSize;
		int si = SSMath.ceil((float) length / (float) pageAddrSize / (float) pageSize);//s % 1 == 0 ? (int) s : (int) s + 1;
		int lenBytes = length * bytes;

		for (int i = 0; i < si; i++) {
			if(addrToNext == -1) {
				break;
			}
			
			for (int x = 0; x < pageAddrSize; x++) {
				long addr = addrToNext + (x * Long.BYTES);
				long addrToPage = u.getAddress(addr);
				if (lenBytes - index <= 0) {
					return buffer;
				}
				if (lenBytes - index < pageSizeBytes) {
					u.copyMemory(addrToPage, bufferAddress + index, lenBytes - index);
					return buffer;
				}
				u.copyMemory(addrToPage, bufferAddress + index, pageSizeBytes);
				index += pageSizeBytes;
			}

			addrToNext = u.getAddress(addrToNext + pageAddrBytes);
		}

		return buffer;
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
