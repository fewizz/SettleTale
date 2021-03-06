package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFWKeyCallback;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class KeyListener extends GLFWKeyCallback {
	public static final KeyListener INSTANCE = new KeyListener();
	
	private static final Map<Thread, HashIntObjMap<KeyState>> KEY_UPDATES = new ConcurrentHashMap<>();
	private static final ThreadLocal<HashIntObjMap<KeyState>> KEY_STATES = new ThreadLocal<HashIntObjMap<KeyState>>() {
		@Override
		protected HashIntObjMap<KeyState> initialValue() {
			return HashIntObjMaps.newMutableMap(60);
		}
	};

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		KeyState state = KeyState.getStateFromAction(action);
		
		KEY_UPDATES.forEach((thread, updateMap) -> {
			updateMap.put(key, state);
		});
	}

	public static boolean isKeyPressed(int key) {
		KeyState state = KEY_STATES.get().get(key);
		
		if (state == KeyState.PRESS || state == KeyState.PRESS_FIRSTLY) {
			return true;
		}

		return false;
	}
	
	public static boolean isKeyPressedFirstly(int key) {
		KeyState state = KEY_STATES.get().get(key);

		if (state == KeyState.PRESS_FIRSTLY) {
			return true;
		}

		return false;
	}
	
	public static boolean isKeyPressedLastly(int key) {
		KeyState state = KEY_STATES.get().get(key);

		if (state == KeyState.PRESS_END) {
			return true;
		}

		return false;
	}

	public static void updateForCurrentThread() {
		Thread t = Thread.currentThread();
		final HashIntObjMap<KeyState> states = KEY_STATES.get();
		KEY_UPDATES.computeIfAbsent(t, (key) -> HashIntObjMaps.newMutableMap());
		HashIntObjMap<KeyState> updates = KEY_UPDATES.get(t);
		
		states.forEach((int key, KeyState state) -> {
			if(state == KeyState.PRESS_FIRSTLY) {
				states.put(key, KeyState.PRESS);
			}
			if(state == KeyState.PRESS_END) {
				states.put(key, KeyState.UNPRESSED);
			}
		});
		
		if(updates.isEmpty()) {
			return;
		}
		
		updates.forEach((int key, KeyState updatedState) -> {
			KeyState currentState = states.get(key);
			if(currentState == null) {
				currentState = KeyState.UNPRESSED;
			}
			
			if (currentState == KeyState.UNPRESSED && updatedState == KeyState.PRESS) {
				states.put(key, KeyState.PRESS_FIRSTLY);
			}
			if ((currentState == KeyState.PRESS || currentState == KeyState.PRESS_FIRSTLY) && updatedState == KeyState.UNPRESSED) {
				states.put(key, KeyState.PRESS_END);
			}
		});
		updates.clear();
	}

	public enum KeyState {
		UNPRESSED,
		PRESS_FIRSTLY,
		PRESS,
		PRESS_END;

		public static KeyState getStateFromAction(int action) {
			if (action == GLFW_RELEASE) {
				return UNPRESSED;
			}
			return KeyState.PRESS;
		}
	}
}
