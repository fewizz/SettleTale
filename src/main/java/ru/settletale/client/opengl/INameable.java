package ru.settletale.client.opengl;

public interface INameable<T> {
	public T gen();
	public int getID();
	public boolean bind();
	public boolean unbind();
}
