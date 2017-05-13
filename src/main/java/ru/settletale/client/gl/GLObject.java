package ru.settletale.client.gl;

public abstract class GLObject<T> {
	public static final int ID_NOT_GENERATED = -1;
	protected int id = ID_NOT_GENERATED;

	public T gen() {
		id = genInternal();
		return getThis();
	}

	public void delete() {
		deleteInternal();
		id = ID_NOT_GENERATED;
	}

	public int getID() {
		return id;
	}

	@SuppressWarnings("unchecked")
	protected T getThis() {
		return (T) this;
	}

	public boolean isGenerated() {
		return id != ID_NOT_GENERATED;
	}

	public abstract boolean isBase();

	public abstract int genInternal();

	public abstract void deleteInternal();
}
