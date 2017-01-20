package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.IRenderable;
import ru.settletale.client.resource.FontLoader;
import ru.settletale.util.IRegionManagerListener;
import ru.settletale.world.region.Region;

public class WorldRenderer implements IRegionManagerListener, IRenderable{
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static HashLongObjMap<Region> regions;
	public static HashLongObjMap<CompiledRegion> regionsToRender;

	public static final int POSITION = 0;
	public static final int NORMAL = 1;

	public static PrimitiveArray pa = new PrimitiveArray(true, Storage.FLOAT_3, Storage.FLOAT_1);
	static ShaderProgram programSky;

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0.5F, 1F, 1F);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		regions = HashLongObjMaps.newMutableMap();
		regionsToRender = HashLongObjMaps.newMutableMap();
		programSky = new ShaderProgram().gen();
		programSky.attachShaders(new Shader(Shader.Type.VERTEX, "shaders/sky_vs.shader").gen().compile(), new Shader(Shader.Type.FRAGMENT, "shaders/sky_fs.shader").gen().compile());
		programSky.link();
	}

	@Override
	public void render() {
		GL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		GL.viewMatrix.push();
		GL.viewMatrix.rotateDeg(Camera.rotationX, 1, 0, 0);
		GL.viewMatrix.rotateDeg(Camera.rotationY, 0, 1, 0);
		GL.viewMatrix.translate(-Camera.x, -Camera.y, -Camera.z);

		GL.updateTransformUniformBlock();

		GL.debug("World rend after transforms");

		for (Region r : regions.values()) {
			CompiledRegion cr = regionsToRender.get(r.coord);

			if (cr == null) {
				GL.debug("Fill buffers");
				renderRegion(r);
				GL.debug("Fill VBOs");
				cr = new CompiledRegion(r);
				cr.compile(pa);
				GL.debug("Array clear");
				pa.clear();
				regionsToRender.put(r.coord, cr);
			}
			cr.render();

		}

		GL.bindDefaultVAO();
		programSky.bind();
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		
		FontLoader.fontMap.get("fonts/font.fnt").render("aaaa!", 0, 50);
		GL11.glLineWidth(10);
		Drawer.begin(GL_LINES);
		Drawer.color(1, 0, 0, 1);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();
		
		GL.viewMatrix.pop();
		
		GL.debug("World rend end");
	}

	private static void renderRegion(Region r) {
		fillBuffers(r);

		GL.debug("Fill buffers0");

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int x2 = 0; x2 < 2; x2++) {
					for (int z2 = 0; z2 < 2; z2++) {
						int nx = x * 2 + x2;
						int nz = z * 2 + z2;

						int i1 = nx * 33 + nz;
						int i2 = nx * 33 + (nz + 1);
						int i3 = (nx + 1) * 33 + (nz + 1);
						int i4 = (nx + 1) * 33 + nz;

						pa.index(i1);
						pa.index(i2);
						pa.index(i3);
						pa.index(i4);
					}
				}
			}
		}
	}

	static Vector3f normal = new Vector3f();
	static Vector3f temp = new Vector3f();
	static Vector3f v1 = new Vector3f();
	static Vector3f v2 = new Vector3f();
	static Vector3f v3 = new Vector3f();

	private static void fillBuffers(Region r) {

		for (int x = 2; x < 35; x++) {
			for (int z = 2; z < 35; z++) {
				normal.set(0, 0, 0);
				temp.set(0, 0, 0);

				v1.set(x, r.getHeight(x, z), z);

				v2.set(x, r.getHeight(x, z + 1), z + 1);
				v3.set(x + 1, r.getHeight(x + 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				normal.add(temp);

				v2.set(x + 1, r.getHeight(x + 1, z), z);
				v3.set(x, r.getHeight(x, z - 1), z - 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				normal.add(temp);

				v2.set(x, r.getHeight(x, z - 1), z - 1);
				v3.set(x - 1, r.getHeight(x - 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				normal.add(temp);

				v2.set(x - 1, r.getHeight(x - 1, z), z);
				v3.set(x, r.getHeight(x, z + 1), z + 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				normal.add(temp);

				normal.normalize();

				int nx = x - 2;
				int nz = z - 2;

				float px = (nx / 2F);
				float pz = (nz / 2F);

				float pxf = r.x * 16 + px;
				float pzf = r.z * 16 + pz;

				pa.data(POSITION, pxf, r.getHeight(x, z), pzf);
				pa.data(NORMAL, normal.y);

				pa.endVertex();
			}
		}
	}

	@Override
	public void onRegionAdded(Region r) {
		GLThread.addTask(() -> {
			regions.put(r.coord, r);
			r.threads++;
		});

	}

	@Override
	public void onRegionRemoved(Region r) {
		GLThread.addTask(() -> {
			regions.remove(r.coord);
			if (regionsToRender.containsKey(r.coord)) {
				regionsToRender.get(r.coord).clear();
				regionsToRender.remove(r.coord);
			}
			r.threads--;
		});

	}
}
