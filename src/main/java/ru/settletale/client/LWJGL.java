package ru.settletale.client;

public class LWJGL {
	public static void initLWJGL() {
		System.setProperty("org.lwjgl.opengl.capabilities", "static");
		org.lwjgl.system.Library.initialize();
	}
}
