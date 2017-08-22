package ru.settletale.client.gl;

public abstract class GLObject<SELF> {
	public static final int ID_NOT_GENERATED = -1;
	public static final int ID_DEFAULT = 0;
	private int id = ID_NOT_GENERATED;

	protected SELF gen() {
		if(isGenerated()) {
			throw new Error("Already generated");
		}
		id = genInternal();
		return getThis();
	}
	
	protected void check() {
		if(!isGenerated()) {
			throw new Error("GLObject is not generated");
		}
	}

	public void delete() {
		deleteInternal();
		id = ID_NOT_GENERATED;
	}

	protected int getID() {
		return id;
	}
	
	protected void setID(int id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	protected SELF getThis() {
		return (SELF) this;
	}

	public boolean isGenerated() {
		return getID() != ID_NOT_GENERATED;
	}

	protected abstract int genInternal();

	protected abstract void deleteInternal();
}
