package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.vertex.AttributeType;
import ru.settletale.client.vertex.VertexAttributeArrayIndexed;
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

	public static final int POSITION = 0;
	public static final int NORMAL = 1;

	public static final VertexAttributeArrayIndexed VERTEX_ARRAY = new VertexAttributeArrayIndexed(AttributeType.FLOAT_3, AttributeType.FLOAT_1);
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
		GL.PROJ_MATRIX.perspectiveDeg(120F, (float) Window.width / (float) Window.height, 0.1F, 1000);
		GL.VIEW_MATRIX.push();
		GL.PROJ_MATRIX.push();
		
		GL.VIEW_MATRIX.rotateDeg(Camera.rotationX, 1, 0, 0);
		GL.VIEW_MATRIX.rotateDeg(Camera.rotationY, 0, 1, 0);
		GL.VIEW_MATRIX.translate(-Camera.position.x, -Camera.position.y, -Camera.position.z);

		GL.updateTransformUniformBlock();

		GL.debug("World rend after transforms");

		for (Region r : REGIONS.values()) {
			CompiledRegion cr = REGIONS_TO_RENDER.get(r.coord);

			if (cr == null) {
				GL.debug("Fill buffers");
				renderRegion(r);
				GL.debug("Fill VBOs");
				cr = new CompiledRegion(r);
				cr.compile(VERTEX_ARRAY);
				GL.debug("Array clear");
				VERTEX_ARRAY.clear();
				REGIONS_TO_RENDER.put(r.coord, cr);
			}
			cr.render();

		}

		GL.bindDefaultVAO();
		programSky.bind();
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

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
		
		GL.updateTransformUniformBlock();

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.ortho2D(0, Window.width, 0, Window.height);
		GL.VIEW_MATRIX.identity();
		GL.updateTransformUniformBlock();
		
		FontRenderer.setSize(25);
		FontRenderer.setColor(Color.WHITE);
		FontRenderer.getPosition().set(10, Window.height - 30, 0);
		FontRenderer.setText("FPS: " + MainRenderer.lastFPSCount);
		FontRenderer.render();
		
		GL.PROJ_MATRIX.pop();
		GL.VIEW_MATRIX.pop();

		GL.debug("World rend end");
	}

	private static void renderRegion(Region r) {
		fillBuffers(r);

		GL.debug("Fill buffers0");

		for (int x = 0; x < 16; x++) {
			int nx = x * 2;
			
			for (int z = 0; z < 16; z++) {
				int nz = z * 2;

				int i1 = nx * 33 + nz;
				int i2 = i1 + 1;
				int i3 = i2 + 33;
				int i4 = i1 + 33;

				for (int x2 = 0; x2 < 2; x2++) {

					for (int z2 = 0; z2 < 2; z2++) {
						VERTEX_ARRAY.index(i1++);
						VERTEX_ARRAY.index(i2++);
						VERTEX_ARRAY.index(i3++);
						VERTEX_ARRAY.index(i4++);
					}

					i1 -= 2;
					i2 -= 2;
					i3 -= 2;
					i4 -= 2;

					i1 += 33;
					i2 += 33;
					i3 += 33;
					i4 += 33;
				}
			}
		}
	}

	static final Vector3f NORMAL_TEMP = new Vector3f();
	static final Vector3f TEMP = new Vector3f();
	static final Vector3f V1_TEMP = new Vector3f();
	static final Vector3f V2_TEMP = new Vector3f();
	static final Vector3f V3_TEMP = new Vector3f();

	private static void fillBuffers(Region r) {
		for (int x = 0; x < 33; x++) {
			int nx = x;
			float px = (nx / 2F);
			float pxf = r.x * 16 + px;
			float pzf = r.z * 16;

			for (int z = 0; z < 33; z++) {
				NORMAL_TEMP.set(0);
				TEMP.set(0);

				V1_TEMP.set(x, r.getHeight(x, z), z);

				V2_TEMP.set(x, r.getHeight(x, z + 1), z + 1);
				V3_TEMP.set(x + 1, r.getHeight(x + 1, z), z);
				V2_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x + 1, r.getHeight(x + 1, z), z);
				V3_TEMP.set(x, r.getHeight(x, z - 1), z - 1);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x, r.getHeight(x, z - 1), z - 1);
				V3_TEMP.set(x - 1, r.getHeight(x - 1, z), z);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x - 1, r.getHeight(x - 1, z), z);
				V3_TEMP.set(x, r.getHeight(x, z + 1), z + 1);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				NORMAL_TEMP.normalize();

				VERTEX_ARRAY.data(POSITION, pxf, r.getHeight(x, z), pzf);
				VERTEX_ARRAY.data(NORMAL, NORMAL_TEMP.y);

				VERTEX_ARRAY.endVertex();
				
				pzf += 0.5F;
			}
		}
	}

	@Override
	public void onRegionAdded(Region r) {
		GLThread.addTask(() -> {
			REGIONS.put(r.coord, r);
			r.threads++;
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
			r.threads--;
		});

	}
}
