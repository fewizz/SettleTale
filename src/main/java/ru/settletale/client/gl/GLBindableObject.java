package ru.settletale.client.gl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.render.Renderer;

public abstract class GLBindableObject<T> extends GLObject<T> {
	protected final GlobalID globalID;
	private static final Map<Class<?>, Class<?>> CLASS_TO_CLASS = new HashMap<>();
	private static final Map<Class<?>, GlobalID> CLASS_TO_GLOBAL_ID = new HashMap<>();
	
	public GLBindableObject() {
		Class<?> clazz = CLASS_TO_CLASS.get(getClass());

		if (clazz == null) {
			clazz = getBaseClass();
			CLASS_TO_CLASS.put(getClass(), clazz);
		}

		GlobalID globalID = CLASS_TO_GLOBAL_ID.get(clazz);
		if (globalID == null) {
			globalID = new GlobalID();
			CLASS_TO_GLOBAL_ID.put(clazz, globalID);
		}
		
		this.globalID = globalID;
	}
	
	private Class<?> getBaseClass() {
		Class<?> baseClass = getClass();

		if (Renderer.DEBUG_GL)
			System.out.println(baseClass.getName());

		for (;;) {
			Method isBaseMethod = null;

			try {
				for(Method m : baseClass.getDeclaredMethods()) {
					if(m.getName().equals("isBase")) {
						isBaseMethod = m;
					}
				}
				
				if (isBaseMethod != null && (boolean) isBaseMethod.invoke(this)) {
					break;
				}
				
				baseClass = baseClass.getSuperclass();

				if (baseClass == Object.class) {
					throw new Error("Base class for " + getClass().getName() + " is not found!");
				}

				continue;

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}

		}

		if (!GLObject.class.isAssignableFrom(baseClass)) {
			throw new Error("Class + " + baseClass.getName() + " is not base!");
		}

		if (Renderer.DEBUG_GL) {
			System.out.println(": " + baseClass.getName());
		}

		return baseClass;
	}
	
	protected void setLastGlobalID(int id) {
		globalID.set(id);
	}

	protected int getLastGlobalID() {
		return globalID.get();
	}
	
	public boolean isBound() {
		return getID() == getLastGlobalID();
	}
	
	public void bind() {
		if (!isGenerated()) {
			throw new Error("Object is not generated!");
		}
		if (getLastGlobalID() == getID()) {
			return;
		}
		bindInternal();
		setLastGlobalID(getID());
	}

	@Override
	public void delete() {
		bind();
		deleteInternal();

		if (getLastGlobalID() == getID()) {
			setLastGlobalID(ID_DEFAULT);
		}
		setID(ID_NOT_GENERATED);
	}

	protected abstract void bindInternal();
}
