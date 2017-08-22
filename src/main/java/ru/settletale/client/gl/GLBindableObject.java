package ru.settletale.client.gl;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.util.MutableInt;

public abstract class GLBindableObject<SELF> extends GLObject<SELF> {
	private static final Map<Class<?>, MutableInt> GLOBAL_IDS = new HashMap<>();
	protected final MutableInt globalID;
	
	public GLBindableObject(Class<?> baseClass) {
		 globalID = GLOBAL_IDS.computeIfAbsent(baseClass, clazz -> new MutableInt(-1));
	}
	
	public boolean isBound() {
		return getID() == globalID.get();
	}
	
	public void bind() {
		check();
		if (isBound()) {
			return;
		}
		bindInternal();
		globalID.set(getID());
	}

	@Override
	public void delete() {
		bind();
		deleteInternal();

		if (globalID.get() == getID()) {
			globalID.set(ID_DEFAULT);
		}
		setID(ID_NOT_GENERATED);
	}

	protected abstract void bindInternal();
}
