package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFWKeyCallback;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class KeyListener extends GLFWKeyCallback {
	public static final KeyListener INSTANCE = new KeyListener();
	
	private static final Map<Thread, HashIntObjMap<KeyState>> KEY_UPDATES = new HashMap<>();
	private static final ThreadLocal<HashIntObjMap<KeyState>> KEY_STATES = new ThreadLocal<HashIntObjMap<KeyState>>() {
		@Override
		protected HashIntObjMap<KeyState> initialValue() {
			return HashIntObjMaps.newMutableMap();
		}
	};

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		Thread t = Thread.currentThread();
		
		HashIntObjMap<KeyState> updates = KEY_UPDATES.get(t);
		
		if(updates == null) {
			updates = HashIntObjMaps.newMutableMap();
			KEY_UPDATES.put(t, updates);
		}
		
		KeyState state = KeyState.getStateFromAction(action);
		
		KEY_UPDATES.forEach((thread, updateMap) -> {
			updateMap.put(key, state);
		});
	}

	public static boolean isKeyPressed(int key) {
		KeyState state = KEY_STATES.get().get(key);
		
		if (state == KeyState.PRESSED || state == KeyState.PRESSED_FIRST) {
			return true;
		}

		return false;
	}
	
	public static boolean isKeyPressedFirst(int key) {
		KeyState state = KEY_STATES.get().get(key);

		if (state == KeyState.PRESSED_FIRST) {
			return true;
		}

		return false;
	}
	
	public static boolean isKeyPressedLast(int key) {
		KeyState state = KEY_STATES.get().get(key);

		if (state == KeyState.PRESSED_LAST) {
			return true;
		}

		return false;
	}

	public static void updateForCurrentThread() {
		Thread t = Thread.currentThread();
		final HashIntObjMap<KeyState> states = KEY_STATES.get();
		HashIntObjMap<KeyState> updates = KEY_UPDATES.get(t);
		
		if(updates == null) {
			updates = HashIntObjMaps.newMutableMap();
			KEY_UPDATES.put(t, updates);
			return; //Bcs map is empty
		}
		
		states.forEach((int key, KeyState state) -> {
			if(state == KeyState.PRESSED_FIRST) {
				states.put(key, KeyState.PRESSED);
			}
			if(state == KeyState.PRESSED_LAST) {
				states.put(key, KeyState.UNPRESSED);
			}
		});
		
		if(updates.isEmpty()) {
			return;
		}
		
		updates.forEach((int key, KeyState state) -> {
			KeyState currentState = states.get(key);
			if(currentState == null) {
				currentState = KeyState.UNPRESSED;
			}
			
			if (currentState == KeyState.UNPRESSED && state == KeyState.PRESSED) {
				states.put(key, KeyState.PRESSED_FIRST);
			}
			if ((currentState == KeyState.PRESSED || currentState == KeyState.PRESSED_FIRST) && state == KeyState.UNPRESSED) {
				states.put(key, KeyState.PRESSED_LAST);
			}
		});
		updates.clear();
	}

	public enum KeyState {
		UNPRESSED,
		PRESSED_FIRST,
		PRESSED,
		PRESSED_LAST;

		public static KeyState getStateFromAction(int action) {
			if (action == GLFW_RELEASE) {
				return UNPRESSED;
			}
			return KeyState.PRESSED;
		}
	}
}
