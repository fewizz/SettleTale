package ru.settletale.util;

import ru.settletale.memory.MemoryBlock;

public class IndexArray {
	private static final int NOT_SET = -1;
	private final MemoryBlock mb;
	public final int length;
	int maxPositionOccupied;
	int elementCount;

	public static void main(String[] args) {
		IndexArray arr = new IndexArray(10);

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
		System.out.println(arr.getFirstPositionOfIndex(5));
	}

	public IndexArray(int size) {
		this.length = size;
		mb = new MemoryBlock().allocate(size * Integer.BYTES);
		init();
	}
	
	public void init() {
		maxPositionOccupied = -1;
		elementCount = 0;
		mb.set(NOT_SET);
	}

	public void set(int position, int index) {
		if(position > maxPositionOccupied) {
			this.maxPositionOccupied = position;
			elementCount++;
		}
		else if(get(position) == NOT_SET) {
			elementCount++;
		}
		
		mb.putIntI(position, index);
	}

	public int setNextFreePositionIfAbsent(int index) {
		int position = getFirstPositionOfIndex(index);

		if (position == -1) {
			setNextFreePosition(index);
		}

		return position;
	}

	public int setNextFreePosition(int index) {
		int position = getNextFreePosition();
		set(position, index);
		return position;
	}

	public void remove(int position) {
		if(get(position) != NOT_SET) {
			elementCount--;
			mb.putIntI(position, NOT_SET);
		}
		
		if(position == maxPositionOccupied) {
			for(int p = position - 1; p >= 0; p--) {
				if(get(p) != NOT_SET) {
					maxPositionOccupied = p;
				}
			}
		}
	}

	public int get(int position) {
		return mb.getIntI(position);
	}

	public int getFirstPositionOfIndex(int index) {
		for(int position = 0; position <= maxPositionOccupied; position++) {
			if(get(position) == index) {
				return position;
			}
		}

		return -1;
	}

	public int getNextFreePosition() {
		for (int position = 0; position <= maxPositionOccupied; position++) {
			if (mb.getIntI(position) == NOT_SET)
				return position;
		}
		
		return -1;
	}

	public void forEach(IntIntConsumer action) {
		for (int position = 0; position <= maxPositionOccupied; position++) {
			int index = mb.getIntI(position);
			if (index == NOT_SET)
				continue;

			action.accept(position, index);
		}
	}

	public void clear() {
		init();
	}

	public void delete() {
		mb.free();
	}
	
	public boolean isEmpty() {
		return elementCount == 0;
	}

	@FunctionalInterface
	public static interface IntIntConsumer {
		void accept(int position, int index);
	}
}
