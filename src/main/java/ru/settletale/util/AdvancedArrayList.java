package ru.settletale.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public class AdvancedArrayList<T> extends ArrayList<T> implements AdvancedList<T> {
	static Field fieldElementData;
	private static final long serialVersionUID = 3488756748637486542L;
	
	static {
		try {
			fieldElementData = ArrayList.class.getDeclaredField("elementData");
			fieldElementData.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addIfAbsent(T o) {
		if(!contains(o)) {
			add(o);
		}
	}
	
	public void setWithForce(int index, T obj) {
		ensureCapacity(index + 1);
		set(index, obj);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void forEachIndexed(Consumer<T> c) {
		Objects.requireNonNull(c);
        final int modCount = this.modCount;
        final Object[] array = getElementData();
        
        for (int size = this.size(), n = 0; this.modCount == modCount && n < size; ++n) {
            c.accept(n, (T) array[n]);
        }
        if (this.modCount != modCount) {
            throw new ConcurrentModificationException();
        }
	}
	
	private Object[] getElementData() {
		try {
			return (Object[]) fieldElementData.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@FunctionalInterface
	public static interface Consumer<T> {
		public void accept(int index, T t);
	}
}
