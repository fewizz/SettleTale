package ru.settletale.client.render.util;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Shader.ShaderType;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.client.resource.loader.ShaderSourceLoader;

public class GLUtils {
	public static String getErrorNameFromHex(int hex) {
		switch (hex) {
			case GL_INVALID_ENUM:
				return "GL_INVALID_ENUM";
			case GL_INVALID_VALUE:
				return "GL_INVALID_VALUE";
			case GL_INVALID_OPERATION:
				return "GL_INVALID_OPERATION";
			case GL_STACK_OVERFLOW:
				return "GL_STACK_OVERFLOW";
			case GL_STACK_UNDERFLOW:
				return "GL_STACK_UNDERFLOW";
			case GL_OUT_OF_MEMORY:
				return "GL_OUT_OF_MEMORY";
			case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
				return "GL_INVALID_FRAMEBUFFER_OPERATION";
			case GL45.GL_CONTEXT_LOST:
				return "GL_CONTEXT_LOST";
			default:
				return null;
		}
	}
	
	public static void linkShadersToProgram(ShaderProgram program, String vsLocation, String fsLocation) {
		if(!program.isGenerated())
			program.gen();
		
		String vs = ShaderSourceLoader.SHADER_SOURCES.get(vsLocation);
		String fs = ShaderSourceLoader.SHADER_SOURCES.get(fsLocation);
		
		if(vs == null) {
			vs = ResourceManager.SHADER_SOURCE_LOADER.loadResource(vsLocation);
		}
		if(fs == null) {
			fs = ResourceManager.SHADER_SOURCE_LOADER.loadResource(fsLocation);
		}
		
		program.attachShader(new Shader().gen(ShaderType.VERTEX).source(vs));
		program.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(fs));
		program.link();
	}
}
