package ru.settletale.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;

class KeyListener extends GLFWKeyCallback {
	private static final Map<Integer, KeyState> keyStates = new ConcurrentHashMap<>();

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW_RELEASE) {
			keyStates.put(key, KeyState.PRESSED_LAST);
			return;
		}

		KeyState state = keyStates.get(key);

		if ((state == null || state == KeyState.UNPRESSED) && action == GLFW_PRESS) {
			keyStates.put(key, KeyState.PRESSED_FIRST);
		}
	}
	
	public static boolean isKeyPressed(int key) {
		KeyState state = keyStates.get(key);
		
		if(state == KeyState.PRESSED || state == KeyState.PRESSED_FIRST) {
			return true;
		}
		
		return false;
	}

	public static void update() {
		for (Integer key : keyStates.keySet()) {
			KeyState state = keyStates.get(key);

			if (state == KeyState.PRESSED_FIRST) {
				keyStates.put(key, KeyState.PRESSED);
			}
			if (state == KeyState.PRESSED_LAST) {
				keyStates.put(key, KeyState.UNPRESSED);
			}
		}
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
			if (action == GLFW_PRESS) {
				return PRESSED_FIRST;
			}
			if (action == GLFW_REPEAT) {
				return PRESSED;
			}
			return null;
		}
	}
}
