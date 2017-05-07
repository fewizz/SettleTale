package ru.settletale.client;

import org.lwjgl.system.Library;

public class LWJGL {
	public static void initLWJGL() {
		//System.setProperty("org.lwjgl.opengl.capabilities", "static");
		//System.setProperty("org.lwjgl.util.Debug", "true");
		//System.setProperty("org.lwjgl.util.NoChecks", "true");
		Library.initialize();
	}
}
