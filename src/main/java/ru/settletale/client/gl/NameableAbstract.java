package ru.settletale.client.gl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class NameableAbstract<T> implements INameable<T> {
	protected int id = -1;
	protected GlobalID idGlobal;
	private static final Map<Class<?>, Class<?>> CLASS_TO_CLASS = new HashMap<>();
	private static final Map<Class<?>, GlobalID> CLASS_TO_GLOBAL_ID = new HashMap<>();

	public NameableAbstract() {
		Class<?> clazz = CLASS_TO_CLASS.get(getClass());

		if (clazz == null) {
			clazz = getNameableBaseClass();
			CLASS_TO_CLASS.put(getClass(), clazz);
		}

		idGlobal = CLASS_TO_GLOBAL_ID.get(clazz);

		if (idGlobal == null) {
			idGlobal = new GlobalID();

			CLASS_TO_GLOBAL_ID.put(clazz, idGlobal);
		}
	}

	@Override
	public T gen() {
		id = genInternal();
		return getThis();
	}
	
	@Override
	public void delete() {
		bind();
		deleteInternal();
		setLastGlobalID(-1);
		id = -1;
	}

	@Override
	public int getID() {
		return id;
	}
	
	private void setLastGlobalID(int id) {
		idGlobal.set(id);
	}

	private int getLastGlobalID() {
		return idGlobal.get();
	}

	@SuppressWarnings("unchecked")
	protected T getThis() {
		return (T) this;
	}

	@Override
	public boolean bind() {
		if (!isGenerated()) {
			throw new Error("Object is not generated!");
		}
		if (getLastGlobalID() == id) {
			return false;
		}
		bindInternal();
		setLastGlobalID(id);
		return true;
	}

	public boolean isBound() {
		return id == getLastGlobalID();
	}

	@Override
	public boolean isGenerated() {
		return id != -1;
	}

	@Override
	public boolean unbind() {
		if (getLastGlobalID() == 0) {
			return false;
		}
		unbindInternal();
		setLastGlobalID(0);
		return true;
	}

	private Class<?> getNameableBaseClass() {
		Class<?> result = getClass();

		if (GL.DEBUG)
			System.out.println(result.getName());

		for (;;) {
			Method method = null;

			try {
				method = result.getDeclaredMethod("isBase");
			} catch (NoSuchMethodException e) {} catch (SecurityException e) {}

			try {
				if (method != null && (boolean) method.invoke(this)) {
					break;
				}
				else {
					result = result.getSuperclass();

					if (result == Object.class) {
						throw new Error("Base class for " + getClass().getName() + " is not found!");
					}

					continue;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}

		}

		if (!NameableAbstract.class.isAssignableFrom(result)) {
			throw new Error("Class + " + result.getName() + " is not base!");
		}

		if (GL.DEBUG)
			System.out.println(": " + result.getName());

		return result;
	}

	public abstract boolean isBase();

	public abstract int genInternal();

	public abstract void bindInternal();

	public abstract void unbindInternal();
	
	public abstract void deleteInternal();
}
