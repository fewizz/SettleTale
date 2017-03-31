package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.render.Color;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.FontRenderer;
import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.MainRenderer;
import ru.settletale.client.render.RenderLayerList;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.world.region.IRegionManagerListener;
import ru.settletale.world.region.Region;

public class WorldRenderer implements IRegionManagerListener {
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static final HashLongObjMap<Region> REGIONS = HashLongObjMaps.newMutableMap();
	public static final HashLongObjMap<CompiledRegion> REGIONS_TO_RENDER = HashLongObjMaps.newMutableMap();

	static ShaderProgram programSky;

	static RenderLayerList lineList;

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0F, 0F, 1F);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		programSky = new ShaderProgram().gen();
		programSky.attachShader(ShaderLoader.SHADERS.get("shaders/sky.vs"));
		programSky.attachShader(ShaderLoader.SHADERS.get("shaders/sky.fs"));
		programSky.link();
	}

	public static void render() {
		GL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.perspectiveDeg(95F, (float) Window.width / (float) Window.height, 0.1F, 1000);
		GL.VIEW_MATRIX.push();
		GL.PROJ_MATRIX.push();

		GL.VIEW_MATRIX.rotateDeg(Camera.rotationX, 1, 0, 0);
		GL.VIEW_MATRIX.rotateDeg(Camera.rotationY, 0, 1, 0);
		GL.VIEW_MATRIX.translate(-Camera.position.x, -Camera.position.y, -Camera.position.z);
		GL.debug("First translates");

		GL.updateTransformUniformBlock();

		GL.debug("World rend after transforms");

		for (Region r : REGIONS.values()) {
			CompiledRegion cr = REGIONS_TO_RENDER.get(r.coord);

			if (cr == null) {
				GL.debug("Fill buffers");
				GL.debug("Fill VBOs");
				cr = new CompiledRegion(r);
				cr.compile();
				GL.debug("Array clear");
				REGIONS_TO_RENDER.put(r.coord, cr);
			}
			cr.render();

		}

		GL.bindDefaultVAO();
		programSky.bind();
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL.debug("Render sky end");

		GL.VIEW_MATRIX.push();
		GL.VIEW_MATRIX.translate(0, 50, 0);
		GL.VIEW_MATRIX.scale(5);
		GL.updateTransformUniformBlock();
		GL.VIEW_MATRIX.pop();
		GL11.glLineWidth(10);
		Drawer.begin(GL_LINES);
		Drawer.color(Color.RED);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();

		Drawer.begin(GL_QUADS);
		Drawer.color(Color.WHITE);
		Drawer.texture(TextureLoader.TEXTURES.get("textures/grass.png"));
		Drawer.uv(0, 0);
		Drawer.vertex(0, 50, 0);
		Drawer.uv(0, 1);
		Drawer.vertex(0, 100, 0);
		Drawer.uv(1, 1);
		Drawer.vertex(50, 100, 0);
		Drawer.uv(1, 0);
		Drawer.vertex(50, 50, 0);

		Drawer.texture(TextureLoader.TEXTURES.get("textures/tom.png"));
		Drawer.uv(0, 0);
		Drawer.vertex(50, 50, 0);
		Drawer.uv(0, 1);
		Drawer.vertex(50, 100, 0);
		Drawer.uv(1, 1);
		Drawer.vertex(150, 100, 0);
		Drawer.uv(1, 0);
		Drawer.vertex(150, 50, 0);
		Drawer.draw();
		
		Drawer.begin(GL11.GL_LINES);
		Drawer.vertex(-50, 50, 50);
		Drawer.vertex(-50, 50, 0);
		Drawer.draw();
		
		Drawer.color(Color.RED);
		Drawer.begin(GL11.GL_TRIANGLES);
		Drawer.vertex(-20, 25, 20);
		Drawer.vertex(-50, 75, 20);
		Drawer.vertex(-70, 25, 20);
		Drawer.draw();

		GL.updateTransformUniformBlock();

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.ortho2D(0, Window.width, 0, Window.height);
		GL.VIEW_MATRIX.identity();
		GL.updateTransformUniformBlock();

		FontRenderer.setSize(25);
		FontRenderer.setColor(Color.WHITE);
		FontRenderer.getPosition().set(10, Window.height - 30, 0);
		FontRenderer.setText("FPS: " + MainRenderer.lastFPS);
		FontRenderer.render();

		GL.PROJ_MATRIX.pop();
		GL.VIEW_MATRIX.pop();

		GL.debug("World rend end");
	}

	@Override
	public void onRegionAdded(Region r) {
		GLThread.addTask(() -> {
			r.increaseThreadUsage();
			REGIONS.put(r.coord, r);
		});

	}

	@Override
	public void onRegionRemoved(Region r) {
		GLThread.addTask(() -> {
			REGIONS.remove(r.coord);
			if (REGIONS_TO_RENDER.containsKey(r.coord)) {
				REGIONS_TO_RENDER.get(r.coord).clear();
				REGIONS_TO_RENDER.remove(r.coord);
			}
			r.decreaseThreadUsage();
		});

	}
}
