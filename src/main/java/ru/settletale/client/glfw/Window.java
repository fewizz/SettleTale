package ru.settletale.client.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.system.MemoryUtil;

public class Window {
	int width;
	int height;
	long id;
	
	public Window() {
	}
	
	public void create(int w, int h, String title) {
		id = glfwCreateWindow(w, h, title, MemoryUtil.NULL, MemoryUtil.NULL);
		
		if (id == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFW.WINDOWS.put(id, this);
		resize(w, h);
	}

	void resize(int w, int h) {
		width = w;
		height = h;
	}
	
	public void show() {
		glfwShowWindow(id);
	}
	
	public void makeContextCurrent() {
		glfwMakeContextCurrent(id);
	}
	
	public void swapInterval(int interval) {
		glfwSwapInterval(interval);
	}
	
	public void swapBuffers() {
		glfwSwapBuffers(id);
	}
	
	public void setPos(int x, int y) {
		glfwSetWindowPos(id, x, y);
	}
	
	public void setCursorPos(double x, double y) {
		glfwSetCursorPos(id, x, y);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setKeyCallback(GLFWKeyCallbackI c) {
		glfwSetKeyCallback(id, c);
	}
	
	public void setMouseButtonCallback(GLFWMouseButtonCallbackI c) {
		glfwSetMouseButtonCallback(id, c);
	}
	
	public void setFramebufferSizeCallback(GLFWFramebufferSizeCallbackI c) {
		glfwSetFramebufferSizeCallback(id, c);
		c.invoke(id, width, height);
	}
	
	public void setCursorPosCallback(GLFWCursorPosCallbackI c) {
		glfwSetCursorPosCallback(id, c);
	}
	
	static class FrameBufferSizeCallbackWrapper implements GLFWFramebufferSizeCallbackI {
		final GLFWFramebufferSizeCallbackI main;
		Window window;
		
		public FrameBufferSizeCallbackWrapper(Window window, GLFWFramebufferSizeCallbackI main) {
			this.window = window;
			this.main = main;
		}

		@Override
		public void invoke(long window, int width, int height) {
			main.invoke(window, width, height);
			this.window.resize(width, height);
		}
	}
}
