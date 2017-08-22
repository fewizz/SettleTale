package ru.settletale.util;

public class MutableInt {
	private int id;
	
	public MutableInt() {
	}
	
	public MutableInt(int id) {
		this.id = id;
	}
	
	public int get() {
		return id;
	}
	
	public void set(int id) {
		this.id = id;
	}
}
