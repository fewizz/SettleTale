package ru.settletale.client.gl;

public interface INameable<T> {
	public T gen();
	public int getID();
	public boolean bind();
	public boolean unbind();
}
