package wrap.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class GLFW {
	final static HashLongObjMap<Window> WINDOWS = HashLongObjMaps.newMutableMap();
	
	public static void init() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
	}
	
	public static void pollEvents() {
		glfwPollEvents();
	}
	
	static void onWindowCreated(Window w) {
		WINDOWS.put(w.id, w);
	}
	
	public static Window getWindow(long id) {
		return WINDOWS.get(id);
	}
}
