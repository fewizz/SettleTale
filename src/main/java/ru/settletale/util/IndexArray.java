package ru.settletale.util;

import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryUtil.*;

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
		arr.set(3, 7);
		arr.set(4, 5);

		arr.remove(0);

		arr.forEach((p, i) -> {
			System.out.println(i);
		});

		System.out.println(arr.getNextFreePosition());
		System.out.println(arr.getPositionOfIndex(5));
	}

	public IndexArray() {
		buffer = memAllocInt(RESERVE);
		memSet(memAddress(buffer), NOT_SET, buffer.capacity() * Integer.BYTES);
	}

	public void set(int position, int index) {
		checkCapacity(position + 1);

		if (buffer.get(position) == NOT_SET)
			size++;

		if (position - 1 == minOccupied)
			minOccupied++;

		buffer.put(position, index);
	}

	public int setNextFreePositionIfAbsent(int index) {
		int position = getPositionOfIndex(index);

		if (position == -1) {
			position = getNextFreePosition();
			set(position, index);
		}

		return position;
	}

	public void remove(int position) {
		if (buffer.get(position) != NOT_SET)
			size--;

		if (position <= minOccupied)
			minOccupied = position - 1;

		buffer.put(position, NOT_SET);
	}

	public int get(int position) {
		return buffer.get(position);
	}

	public int getPositionOfIndex(int index) {
		int element = 0;

		for (int position = 0; element < size; position++) {
			int ind = buffer.get(position);

			if (ind == index)
				return position;

			if (ind != NOT_SET)
				element++;
		}

		return -1;
	}

	public int getNextFreePosition() {
		for (int position = minOccupied + 1; position < buffer.capacity(); position++) {
			if (buffer.get(position) == NOT_SET)
				return position;
			else
				minOccupied++;
		}

		return -1;
	}

	public void forEach(IntIntConsumer action) {
		int element = 0;

		for (int position = 0; element < size; position++) {
			int index = buffer.get(position);
			if (index == NOT_SET)
				continue;

			action.accept(position, index);
			element++;
		}
	}

	public int getSize() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	private void checkCapacity(int howManySizeShouldBe) {
		if (buffer.capacity() < howManySizeShouldBe) {
			changeBufferSize(howManySizeShouldBe, true);
		}
	}

	private void changeBufferSize(int newSize, boolean saveData) {
		IntBuffer newBuffer = memAllocInt(newSize);
		memSet(memAddress(newBuffer), NOT_SET, newBuffer.capacity() * Integer.BYTES);

		if (saveData)
			memCopy(memAddress(buffer), memAddress(newBuffer), buffer.capacity() * Integer.BYTES);

		memFree(buffer);
		DirectBufferUtils.copyInfo(newBuffer, buffer);
	}

	public void clear() {
		minOccupied = -1;
		size = 0;
		changeBufferSize(RESERVE, false);
	}

	public void delete() {
		memFree(buffer);
	}

	@FunctionalInterface
	public static interface IntIntConsumer {
		void accept(int position, int index);
	}
}
