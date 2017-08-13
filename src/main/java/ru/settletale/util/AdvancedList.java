package ru.settletale.util;

import java.util.List;

import ru.settletale.util.AdvancedArrayList.Consumer;

public interface AdvancedList<E> extends List<E> {
	public void forEachIndexed(Consumer<E> c);
	public void ensureCapacity(int cap);
	
	default public void put(int index, E o) {
		ensureCapacity(index + 1);
		set(index, o);
	}

	default public boolean addIfAbsent(E o) {
		if (!contains(o)) {
			return add(o);
		}

		return false;
	}
	
	default public E getLast() {
		return get(size() - 1);
	}
	
	default public int getLastIndex() {
		return size() - 1;
	}
}
