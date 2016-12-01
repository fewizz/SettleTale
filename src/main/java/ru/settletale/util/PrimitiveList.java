package ru.settletale.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sun.misc.Unsafe;

public class PrimitiveList {
	Unsafe u = Utils.u;
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
	
	public static void main(String[] args) {
		PrimitiveList list = new PrimitiveList(64, Integer.BYTES);
		
		for(int i = 0; i < 512; i++) {
			list.putInt(i, i);
		}
		list.clear();
		for(int i = 0; i < 512; i++) {
			list.putInt(i, i);
		}
		list.updateBuffer();
		
		for(int i = 0; i < 512; i++) {
			System.out.println(list.buffer.getInt(i * Integer.BYTES));
		}
	}

	public PrimitiveList(int pageSize, int typeBytes) {
		this.pageSize = pageSize;
		this.bytes = typeBytes;
		this.pageSizeBytes = this.bytes * pageSize;
		this.pageAddrSize = 64;
		this.pageAddrBytes = this.pageAddrSize * Long.BYTES;
		this.pageAddrBytesFull = pageAddrBytes + Long.BYTES;
		this.firstAddrPage = createNewAddrPage();
		lastAddr = firstAddrPage;
		lastAddrInd = 0;
		this.buffer = ByteBuffer.allocateDirect(pageSizeBytes);
		this.buffer.order(ByteOrder.nativeOrder());
		this.bufferAddress = DirectByteBufferUtils.getBufferAddress(this.buffer);
	}

	public void put(byte value, int index) {
		u.putByte(getValueAdress(index, true), value);
		setLen(index + 1);
	}
	
	public void putFloat(float value, int index) {
		u.putFloat(getValueAdress(index, true), value);
		setLen(index + 1);
	}
	
	public void putInt(int value, int index) {
		u.putInt(getValueAdress(index, true), value);
		setLen(index + 1);
	}
	
	public void putShort(short value, int index) {
		u.putShort(getValueAdress(index, true), value);
		setLen(index + 1);
	}
	
	public byte get(int index) {
		return u.getByte(getValueAdress(index, false));
	}
	
	public float getFloat(int index) {
		return u.getFloat(getValueAdress(index, false));
	}
	
	public int getInt(int index) {
		return u.getInt(getValueAdress(index, false));
	}
	
	private void setLen(int len) {
		length = Math.max(length, len);
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
						return -1;
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
				return -1;
			}
			addrToPage = createNewPage();
			u.putLong(addr, addrToPage);
		}
		return addrToPage + ((index % pageSize) * bytes);
	}

	private long createNewPage() {
		long l = u.allocateMemory(pageSizeBytes);
		clearPage(l);
		return l;
	}
	
	private void clearPage(long addr) {
		u.setMemory(addr, pageSizeBytes, (byte) 0);
	}

	private long createNewAddrPage() {
		long newAddPage = u.allocateMemory(pageAddrBytesFull);
		clearAddrPage(newAddPage);
		return newAddPage;
	}
	
	private void clearAddrPage(long addr) {
		u.setMemory(addr, pageAddrBytesFull, (byte) -1);
	}
	
	public void clear() {
		long addrToNext = firstAddrPage;
		
		for (int i = 0; i < length / pageSize / pageAddrSize; i++) {
			if (addrToNext == -1) {
				break;
			}
			
			for(int x = 0; x < pageAddrSize; x++) {
				long page = u.getAddress(addrToNext + (x * Long.BYTES));
				
				if(page == -1) {
					continue;
				}
				
				if(i != 0) //If not fist page
					u.freeMemory(page);
				else {
					clearPage(page);
				}
			}
			long prev = addrToNext;
			addrToNext = u.getAddress(addrToNext + pageAddrBytes);
			
			if(i == 0) {
				clearAddrPage(prev);
				continue;
			}
			u.freeMemory(prev);
		}
		
		lastAddr = firstAddrPage;
		lastAddrInd = 0;
		length = 0;
	}

	public void updateBuffer() {
		int sizeBytes = length * bytes;
		
		if(buffer.capacity() < sizeBytes) {
			this.bufferAddress = DirectByteBufferUtils.updateBufferSize(this.buffer, sizeBytes, bufferAddress);
		}
		buffer.clear();

		int index = 0;

		long addrToNext = firstAddrPage;

		int si = SSMath.ceil((float) length / (float) pageAddrSize / (float) pageSize);

		for (int i = 0; i < si; i++) {
			if(addrToNext == -1) {
				return;
			}
			
			for (int x = 0; x < pageAddrSize; x++) {
				long addr = addrToNext + (x * Long.BYTES);
				long addrToPage = u.getAddress(addr);
				
				int toCopy = Math.min(pageSizeBytes, sizeBytes - index);
				
				if (toCopy <= 0) {
					return; // Buffer filled
				}
				
				if(addrToPage == -1) 
					u.setMemory(bufferAddress + index, toCopy, (byte) 0);
				else 
					u.copyMemory(addrToPage, bufferAddress + index, toCopy);
				
				index += toCopy;
			}

			addrToNext = u.getAddress(addrToNext + pageAddrBytes);
		}
	}
}
