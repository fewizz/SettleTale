package ru.settletale.util;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

public class IndexArray {
	private static final int NOT_SET = -1;
	private static final int RESERVE = 64;
	private final IntBuffer buffer;
	private int size = 0;
	int minOccupied = -1;

	public static void main(String[] args) {
		IndexArray arr = new IndexArray();

		arr.set(0, 10);
		arr.set(2, 56);
		arr.set(1, 12);
		arr.set(150, 7);
		arr.set(1200, 5);

		arr.remove(0);

		arr.forEach((p, i) -> {
			System.out.println(i);
		});

		System.out.println(arr.getNextFreePosition());
	}

	public IndexArray() {
		buffer = MemoryUtil.memAllocInt(RESERVE);
		MemoryUtil.memSet(MemoryUtil.memAddress(buffer), NOT_SET, buffer.capacity() * Integer.BYTES);
	}

	public void set(int position, int index) {
		checkCapacity(position + 1);

		if (buffer.get(position) == NOT_SET) {
			size++;
		}

		if (position == minOccupied + 1) {
			minOccupied++;
		}

		buffer.put(position, index);
	}
	
	public int setNextFreePositionIfAbsent(int index) {
		int position = getPositionOf(index);
		
		if(position == -1) {
			position = getNextFreePosition();
			set(position, index);
		}
		
		return position;
	}

	public void remove(int position) {
		if (buffer.get(position) != NOT_SET) {
			size--;
		}

		if (position <= minOccupied) {
			minOccupied = position - 1;
		}

		buffer.put(position, NOT_SET);
	}

	public int get(int position) {
		return buffer.get(position);
	}

	public int getPositionOf(int index) {
		for (int position = 0; position < buffer.capacity(); position++) {
			if (buffer.get(position) == index) {
				return position;
			}
		}

		return NOT_SET;
	}

	public int getNextFreePosition() {
		for (int position = minOccupied + 1; position < buffer.capacity(); position++) {
			if (buffer.get(position) == NOT_SET) {
				return position;
			}
			else {
				minOccupied++;
			}
		}

		return -1;
	}

	public void forEach(IntIntConsumer action) {
		int count = 0;

		for (int position = 0; position < buffer.capacity(); position++) {
			if (buffer.get(position) == NOT_SET) {
				continue;
			}

			action.accept(position, buffer.get(position));
			count++;

			if (count >= size) {
				return;
			}
		}
	}

	public int getSize() {
		return size;
	}

	private void checkCapacity(int howManySizeShouldBe) {
		if (buffer.capacity() < howManySizeShouldBe) {
			changeBufferSize(howManySizeShouldBe, true);
		}
	}

	private void changeBufferSize(int newSize, boolean saveData) {
		IntBuffer newBuffer = MemoryUtil.memAllocInt(newSize);
		MemoryUtil.memSet(MemoryUtil.memAddress(newBuffer), NOT_SET, newBuffer.capacity() * Integer.BYTES);
		
		if (saveData) {
			MemoryUtil.memCopy(MemoryUtil.memAddress(buffer), MemoryUtil.memAddress(newBuffer), buffer.capacity() * Integer.BYTES);
		}
		
		MemoryUtil.memFree(buffer);
		DirectBufferUtils.copyInfo(newBuffer, buffer);
	}

	public void clear() {
		minOccupied = -1;
		size = 0;
		changeBufferSize(RESERVE, false);
	}

	public void delete() {
		MemoryUtil.memFree(buffer);
	}

	@FunctionalInterface
	public static interface IntIntConsumer {
		void accept(int position, int index);
	}
}
