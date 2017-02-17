package ru.settletale.client.gl;

public interface INameable<T> {
	public T gen();
	public void delete();
	public int getID();
	public boolean bind();
	public boolean unbind();
	public boolean isGenerated();
}
