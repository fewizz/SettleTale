package ru.settletale.client.opengl;

public abstract class NameableAdapter implements INameable {
	protected int id = -1;
	
	public NameableAdapter(int id) {
		this.id = id;
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public boolean bind() {
		if(getLastBoundID() == id) {
			return false;
		}
		bindInternal();
		setLastBoundID(id);
		return true;
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
	public abstract void bindInternal();
	public abstract void unbindInternal();
}
