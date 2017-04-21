package ru.settletale.util;

import java.util.List;

import ru.settletale.util.AdvancedArrayList.Consumer;

public interface AdvancedList<E> extends List<E> {
	public void forEachIndexed(Consumer<E> c);
	public void addIfAbsent(E o);
}
