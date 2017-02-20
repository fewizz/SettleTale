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
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.MainRenderer;
import ru.settletale.client.render.RenderLayerList;
import ru.settletale.client.resource.FontLoader;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.world.region.IRegionManagerListener;
import ru.settletale.world.region.Region;

public class WorldRenderer implements IRegionManagerListener {
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static final HashLongObjMap<Region> REGIONS = HashLongObjMaps.newMutableMap();;
	public static final HashLongObjMap<CompiledRegion> REGIONS_TO_RENDER = HashLongObjMaps.newMutableMap();;

	public static final int POSITION = 0;
	public static final int NORMAL = 1;

	public static final PrimitiveArray PRIMITIVE_ARRAY = new PrimitiveArray(true, Storage.FLOAT_3, Storage.FLOAT_1);
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
		
		lineList = new RenderLayerList();
		lineList.position(0   , 50, 0).color((byte)255, (byte)0, (byte)0, (byte)255).endVertex();
		lineList.position( 100, 50, 0).color((byte)255, (byte)0, (byte)0, (byte)255).endVertex();
		lineList.compile();
	}

	public static void render() {
		GL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.perspective((float) Math.toRadians(120), (float) Window.width / (float) Window.height, 0.1F, 1000);
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
				cr.compile(PRIMITIVE_ARRAY);
				GL.debug("Array clear");
				PRIMITIVE_ARRAY.clear();
				REGIONS_TO_RENDER.put(r.coord, cr);
			}
			cr.render();

		}

		GL.bindDefaultVAO();
		programSky.bind();
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);

		FontLoader.FONTS.get("fonts/font.fnt").render("ׂוסע רנטפעא =D", 0, 50);
		GL11.glLineWidth(10);
		/*Drawer.begin(GL_LINES);
		Drawer.color(1, 0, 0, 1);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();*/
		
		GL.updateTransformUniformBlock();
		lineList.render(GL11.GL_LINES);

		GL.PROJ_MATRIX.identity();
		GL.PROJ_MATRIX.ortho2D(0, Window.width, 0, Window.height);
		GL.VIEW_MATRIX.identity();
		GL.updateTransformUniformBlock();
		
		FontLoader.FONTS.get("fonts/font.fnt").render("FPS: " + MainRenderer.lastFPSCount, 10, Window.height - 60);
		
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
						PRIMITIVE_ARRAY.index(i1++);
						PRIMITIVE_ARRAY.index(i2++);
						PRIMITIVE_ARRAY.index(i3++);
						PRIMITIVE_ARRAY.index(i4++);
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

		for (int x = 2; x < 35; x++) {
			int nx = x - 2;
			float px = (nx / 2F);
			float pxf = r.x * 16 + px;
			float pzf = r.z * 16;

			for (int z = 2; z < 35; z++) {
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

				PRIMITIVE_ARRAY.data(POSITION, pxf, r.getHeight(x, z), pzf);
				PRIMITIVE_ARRAY.data(NORMAL, NORMAL_TEMP.y);

				PRIMITIVE_ARRAY.endVertex();
				
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
