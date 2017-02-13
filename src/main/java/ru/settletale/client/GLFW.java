package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

public class GLFW {
	
	public static void initGLFW() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		Display.windowID = glfwCreateWindow(1000, 800, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Display.onWindowResize(1000, 800);
		if (Display.windowID == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(Display.windowID, new KeyListener());
		glfwSetFramebufferSizeCallback(Display.windowID, new WindowResizeListener());
		glfwSetCursorPosCallback(Display.windowID, new CursorListener());

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Display.windowID, (vidmode.width() - Display.width) / 2, (vidmode.height() - Display.height) / 2);
		glfwShowWindow(Display.windowID);
	}
}
