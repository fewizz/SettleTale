package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.PlatformClient;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.util.IRegionManageristener;
import ru.settletale.world.region.Region;

public class WorldRenderer implements IRegionManageristener {
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static HashLongObjMap<Region> regions;
	public static HashLongObjMap<CompiledRegion> regionsToRender;
	
	public static final int POSITION = 0;
	public static final int NORMAL = 1;
	
	public static PrimitiveArray pa = new PrimitiveArray(Storage.FLOAT_3, Storage.FLOAT_3);
	static ShaderProgram programSky;

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0.5F, 1F, 1F);
		regions = HashLongObjMaps.newMutableMap();
		regionsToRender = HashLongObjMaps.newMutableMap();
		programSky = new ShaderProgram().gen();
		programSky.attachShaders(
				new Shader(Shader.Type.VERTEX, "shaders/sky_vs.shader").gen().compile(),
				new Shader(Shader.Type.FRAGMENT, "shaders/sky_fs.shader").gen().compile()
				);
		programSky.link();
	}

	public static void render() {
		GL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glEnable(GL_DEPTH_TEST);
		
		GL.viewMatrix.push();
		GL.viewMatrix.rotateDeg(Camera.aX, 1, 0, 0);
		GL.viewMatrix.rotateDeg(Camera.aY, 0, 1, 0);
		GL.viewMatrix.translate(0, -100, 0);
		GL.viewMatrix.translate(-Camera.x, -Camera.y, -Camera.z);
		
		GL.updateTransformUniformBlock();
		
		GL.debug("World rend after transforms");
		
		for(Region r : regions.values()) {
			CompiledRegion cr = regionsToRender.get(r.coord);
			
			if(cr == null) {
				renderRegion(r, pa);
				
				GL.debug("Fill buffers");
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
		
		GL.viewMatrix.pop();
		GL.debug("World rend end");
	}
	
	private static void renderRegion(Region r, PrimitiveArray array) {
		fillBuffers(r);
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for(int x2 = 0; x2 < 2; x2++) {
					for(int z2 = 0; z2 < 2; z2++) {
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
		
		for(int x = 2; x < 35; x++) {
			for(int z = 2; z < 35; z++) {
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
				pa.data(NORMAL, normal.x, normal.y, normal.z);
				
				pa.endVertex();
			}
		}
	}

	@Override
	public void onRegionAdded(Region r) {
		PlatformClient.runInRenderThread(new Runnable() {
			
			@Override
			public void run() {
				regions.put(r.coord, r);
			}
		});
	}

	@Override
	public void onRegionRemoved(Region r) {
		PlatformClient.runInRenderThread(new Runnable() {
			
			@Override
			public void run() {
				regions.remove(r.coord);
				if(regionsToRender.containsKey(r.coord)) {
					regionsToRender.get(r.coord).clear();
					regionsToRender.remove(r.coord);
				}
			}
		});
		
	}
}
