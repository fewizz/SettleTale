package ru.settletale.client.opengl;

public abstract class NameableAdapter<T> implements INameable<T> {
	protected int id = -1;
	
	@Override
	public T gen() {
		id = internalGet();
		return getThis();
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@SuppressWarnings("unchecked")
	protected T getThis() {
		return (T) this;
	}
	
	@Override
	public boolean bind() {
		if(id == -1) {
			throw new Error("Object is not generated!");
		}
		if(getLastBoundID() == id) {
			return false;
		}
		bindInternal();
		setLastBoundID(id);
		return true;
	}
	
	public boolean isBound() {
		return id == getLastBoundID();
	}
	
	@Override
	public boolean unbind() {
		if(getLastBoundID() == 0) {
			return false;
		}
		unbindInternal();
		setLastBoundID(0);
		return true;
	}
	
	public abstract void setLastBoundID(int id);
	public abstract int getLastBoundID();
	public abstract int internalGet();
	public abstract void bindInternal();
	public abstract void unbindInternal();
}
