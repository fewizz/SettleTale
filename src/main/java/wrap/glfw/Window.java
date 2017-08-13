package wrap.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.memory.MemoryBlock;

public class Window {
	private final MemoryBlock tempMemoryBlock = new MemoryBlock().allocate(8);//Integer.BYTES * 4 + Double.BYTES * 2);
	long id;
	
	public Window() {
	}
	
	public void create(int w, int h, String title) {
		id = glfwCreateWindow(w, h, title, MemoryUtil.NULL, MemoryUtil.NULL);
		
		if (id == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		GLFW.onWindowCreated(this);
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
	
	public int getWidth() {
		nglfwGetWindowSize(id, tempMemoryBlock.address(), MemoryUtil.NULL);
		return tempMemoryBlock.getIntI(0);
	}
	
	public int getHeight() {
		nglfwGetWindowSize(id, MemoryUtil.NULL, tempMemoryBlock.address()/* + Integer.BYTES*/);
		return tempMemoryBlock.getIntI(0);
	}
	
	public int getX() {
		nglfwGetWindowPos(id, tempMemoryBlock.address()/* + 2 * Integer.BYTES*/, MemoryUtil.NULL);
		return tempMemoryBlock.getIntI(0);
	}
	
	public int getY() {
		nglfwGetWindowPos(id, MemoryUtil.NULL, tempMemoryBlock.address()/* + 3 * Integer.BYTES*/);
		return tempMemoryBlock.getIntI(0);
	}
	
	public void setCursorPos(double x, double y) {
		glfwSetCursorPos(id, x, y);
	}
	
	public double getCursorX() {
		nglfwGetCursorPos(id, tempMemoryBlock.address()/* + Integer.BYTES * 4*/, MemoryUtil.NULL);
		return tempMemoryBlock.getDouble(0);//Integer.BYTES * 4);
	}
	
	public double getCursorY() {
		nglfwGetCursorPos(id, MemoryUtil.NULL, tempMemoryBlock.address()/* + Integer.BYTES * 4 + Double.BYTES*/);
		return tempMemoryBlock.getDouble(0);//Integer.BYTES * 4 + Double.BYTES);
	}
	
	public void setKeyCallback(GLFWKeyCallbackI c) {
		glfwSetKeyCallback(id, c);
	}
	
	public void setMouseButtonCallback(GLFWMouseButtonCallbackI c) {
		glfwSetMouseButtonCallback(id, c);
	}
	
	public void setFramebufferSizeCallback(GLFWFramebufferSizeCallbackI c) {
		glfwSetFramebufferSizeCallback(id, c);
		//c.invoke(id, getWidth(), getHeight());
	}
	
	public void setCursorPosCallback(GLFWCursorPosCallbackI c) {
		glfwSetCursorPosCallback(id, c);
	}
}
