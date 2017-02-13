package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.GLThread;
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

		GL.viewMatrix.push();
		GL.viewMatrix.rotateDeg(Camera.rotationX, 1, 0, 0);
		GL.viewMatrix.rotateDeg(Camera.rotationY, 0, 1, 0);
		GL.viewMatrix.translate(-Camera.position.x, -Camera.position.y, -Camera.position.z);

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
		Drawer.begin(GL_LINES);
		Drawer.color(1, 0, 0, 1);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();

		GL.viewMatrix.scale(10, 10, 10);
		GL.viewMatrix.translate(0, 0, 0);
		GL.updateTransformUniformBlock();

		GL.viewMatrix.pop();

		GL.debug("World rend end");
	}

	private static void renderRegion(Region r) {
		fillBuffers(r);

		GL.debug("Fill buffers0");

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int nz = z * 2;
				int nx = x * 2;

				int i1 = nx * 33 + nz;
				int i2 = nx * 33 + (nz + 1);
				int i3 = (nx + 1) * 33 + (nz + 1);
				int i4 = (nx + 1) * 33 + nz;

				for (int x2 = 0; x2 < 2; x2++) {

					for (int z2 = 0; z2 < 2; z2++) {
						PRIMITIVE_ARRAY.index(i1);
						PRIMITIVE_ARRAY.index(i2);
						PRIMITIVE_ARRAY.index(i3);
						PRIMITIVE_ARRAY.index(i4);

						i1++;
						i2++;
						i3++;
						i4++;
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
