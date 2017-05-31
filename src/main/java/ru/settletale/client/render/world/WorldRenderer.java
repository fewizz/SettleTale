package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Shader.Type;
import ru.settletale.client.render.Color;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.FontRenderer;
import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.resource.loader.ObjModelLoader;
import ru.settletale.client.resource.loader.ShaderSourceLoader;
import ru.settletale.client.resource.loader.TextureLoader;
import ru.settletale.world.region.IRegionManagerListener;
import ru.settletale.world.region.Region;

public class WorldRenderer implements IRegionManagerListener {
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static final HashLongObjMap<CompiledRegion> REGIONS_TO_RENDER = HashLongObjMaps.newMutableMap();
	static final ShaderProgram PROGRAM_SKY = new ShaderProgram();

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0F, 0F, 1F);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		glCullFace(GL_BACK);
		PROGRAM_SKY.gen();
		PROGRAM_SKY.attachShader(new Shader().gen(Type.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/sky.vs")));
		PROGRAM_SKY.attachShader(new Shader().gen(Type.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/sky.fs")));
		PROGRAM_SKY.link();
	}

	public static void render() {
		GL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		GL.PROJ_MATRIX.identity();
		GL.VIEW_MATRIX.identity();
		GL.PROJ_MATRIX.perspectiveDeg(95F, (float) Window.width / (float) Window.height, 0.1F, 1000F);
		GL.VIEW_MATRIX.rotateDeg(Camera.rotationX, 1, 0, 0);
		GL.VIEW_MATRIX.rotateDeg(Camera.rotationY, 0, 1, 0);
		GL.VIEW_MATRIX.translate((float) -Camera.position.x, (float) -Camera.position.y, (float) -Camera.position.z);
		GL.debug("First translates");

		GL.updateCombinedMatrixUniformBlock();

		GL.debug("World rend after transforms");

		glEnable(GL_CULL_FACE);

		REGIONS_TO_RENDER.forEach((long l, CompiledRegion r) -> {
			if (!r.compiled) {
				GL.debug("Fill buffers");
				GL.debug("Fill VBOs");
				r.compile();
				GL.debug("Array clear");
			}
			r.render();
		});

		glDisable(GL_CULL_FACE);

		GL.updateInversedMatricesUniformBlock();
		GL.bindDefaultVAO();
		PROGRAM_SKY.bind();
		glDrawArrays(GL_QUADS, 0, 4);
		GL.debug("Render sky end");

		/*if (PlatformClient.player.camInter != null) {
			glPointSize(15);
			Drawer.begin(GL_POINTS);
			Drawer.color(Color.WHITE);
			Drawer.vertex((float) PlatformClient.player.camInter.x, (float) PlatformClient.player.camInter.y, (float) PlatformClient.player.camInter.z);
			Drawer.draw();
			
			Drawer.begin(GL_POINTS);
			Drawer.color(Color.RED);
			Vector3d v = new Vector3d(PlatformClient.player.camInter);
			v.add(PlatformClient.player.camInter.normal);
			Drawer.vertex((float) v.x, (float)v.y, (float) v.z);
			Drawer.draw();
		}*/

		GL.VIEW_MATRIX.push();
		GL.VIEW_MATRIX.translate(0, 50, 0);
		GL.VIEW_MATRIX.scale(10F);
		GL.updateMatriciesUniformBlock();
		//ObjModelLoader.MODELS.get("models/dabrovic/sponza.obj").render();
		
		
		GL.VIEW_MATRIX.pop();
		
		glLineWidth(10);
		Drawer.begin(GL_LINES);
		Drawer.COLOR.set(Color.RED);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();

		Drawer.begin(GL_QUADS);
		Drawer.COLOR.set(Color.WHITE);
		Drawer.texture(TextureLoader.TEXTURES.get("textures/grass.png"));
		Drawer.UV.set(0, 0);
		Drawer.vertex(0, 50, 0);
		Drawer.UV.set(0, 1);
		Drawer.vertex(0, 100, 0);
		Drawer.UV.set(1, 1);
		Drawer.vertex(50, 100, 0);
		Drawer.UV.set(1, 0);
		Drawer.vertex(50, 50, 0);

		Drawer.texture(TextureLoader.TEXTURES.get("textures/tom.png"));
		Drawer.UV.set(0, 0);
		Drawer.vertex(50, 50, 0);
		Drawer.UV.set(0, 1);
		Drawer.vertex(50, 100, 0);
		Drawer.UV.set(1, 1);
		Drawer.vertex(150, 100, 0);
		Drawer.UV.set(1, 0);
		Drawer.vertex(150, 50, 0);
		Drawer.draw();

		Drawer.begin(GL_LINES);
		Drawer.vertex(-50, 50, 50);
		Drawer.vertex(-50, 50, 0);
		Drawer.draw();

		Drawer.COLOR.set(Color.RED);
		Drawer.begin(GL_TRIANGLES);
		Drawer.vertex(-20, 25, 20);
		Drawer.vertex(-50, 75, 20);
		Drawer.vertex(-70, 25, 20);
		Drawer.draw();

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.ortho2D(0, Window.width, 0, Window.height);
		GL.VIEW_MATRIX.identity();
		GL.updateCombinedMatrixUniformBlock();

		FontRenderer.setSize(25);
		FontRenderer.setColor(Color.WHITE);
		FontRenderer.getPosition().set(10, Window.height - 30, 0);
		FontRenderer.setText("FPS: " + Renderer.lastFPS);
		FontRenderer.render();

		GL.debug("World rend end");
	}

	@Override
	public void onRegionAdded(Region r) {
		r.increaseThreadUsage();
		GLThread.addTask(() -> {
			REGIONS_TO_RENDER.put(r.coordClamped, new CompiledRegion(r));
		});
	}

	@Override
	public void onRegionRemoved(Region r) {
		GLThread.addTask(() -> {
			if (REGIONS_TO_RENDER.containsKey(r.coordClamped)) {
				REGIONS_TO_RENDER.get(r.coordClamped).clear();
				REGIONS_TO_RENDER.remove(r.coordClamped);
				r.decreaseThreadUsage();
			}
		});

	}
}
