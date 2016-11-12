package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Vector3f;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.PlatformClient;
import ru.settletale.client.opengl.OpenGL;
import ru.settletale.client.opengl.Primitive.Type;
import ru.settletale.client.opengl.PrimitiveArray;
import ru.settletale.util.IRegionManageristener;
import ru.settletale.world.Region;

public class WorldRenderer implements IRegionManageristener {
	static final float scale = 1F;
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static HashLongObjMap<Region> regions;
	public static HashLongObjMap<CompiledRegion> regionsToRender;
	public static PrimitiveArray pa = new PrimitiveArray(Type.Quad);

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0.5F, 1F, 1F);
		glEnable(GL_DEPTH_TEST);
		regions = HashLongObjMaps.newMutableMap();
		regionsToRender = HashLongObjMaps.newMutableMap();
	}

	public static void render() {
		OpenGL.debug("World rend start");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		OpenGL.viewMatrix.push();
		OpenGL.viewMatrix.rotateDeg(Camera.aX, 1, 0, 0);
		OpenGL.viewMatrix.rotateDeg(Camera.aY, 0, 1, 0);
		OpenGL.viewMatrix.translate(0, -100, 0);
		OpenGL.viewMatrix.translate(-Camera.x, -Camera.y, -Camera.z);
		
		OpenGL.updateTransformUniformBlock();
		
		OpenGL.debug("World rend after transforms");
		
		for(Region r : regions.values()) {
			CompiledRegion cr = regionsToRender.get(r.coord);
			
			if(cr == null) {
				renderRegion(r, pa);
				
				OpenGL.debug("Fill buffers");
				ByteBuffer pb = pa.getPositionBuffer();
				ByteBuffer cb = pa.getColorBuffer();
				ByteBuffer nb = pa.getNormalBuffer();
				cr = new CompiledRegion(r);
				cr.compile(r, pb, cb, nb);
				OpenGL.debug("Array clear");
				pa.clear();
				regionsToRender.put(r.coord, cr);
			}
			cr.render();
		}

		OpenGL.viewMatrix.pop();
		OpenGL.debug("World rend end");
	}
	
	private static void renderRegion(Region r, PrimitiveArray array) {
		Vector3f[] normals = findNormals(r);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int tx = r.x * 16 + x;
				int tz = r.z * 16 + z;
				
				int tx2 = tx & 0xF;
				int tz2 = tz & 0xF;
				
				Color c = r.getBiome(x, z).color;
				array.color((byte)c.getRed(), (byte)c.getGreen(), (byte)c.getBlue());
				
				for(int x2 = 0; x2 < 2; x2++) {
					for(int z2 = 0; z2 < 2; z2++) {
						int mx = x * 2 + x2;
						int mz = z * 2 + z2;
						float hx = (float) x2 / 2F;
						float hz = (float) z2 / 2F;
						
						array.position(tx + hx       , r.getHeight(tx2 * 2 + x2    , tz2 * 2 + z2    ), tz + hz       );
						Vector3f v1 = normals[mx * 33 + mz];
						array.normal(v1.x, v1.y, v1.z);
						array.endVertex();
						
						array.position(tx + hx       , r.getHeight(tx2 * 2 + x2    , tz2 * 2 + z2 + 1), tz + hz + 0.5F);
						Vector3f v2 = normals[mx * 33 + mz + 1];
						array.normal(v2.x, v2.y, v2.z);
						array.endVertex();
						
						array.position(tx + hx + 0.5F, r.getHeight(tx2 * 2 + x2 + 1, tz2 * 2 + z2 + 1), tz + hz + 0.5F);
						Vector3f v3 = normals[(mx + 1) * 33 + mz + 1];
						array.normal(v3.x, v3.y, v3.z);
						array.endVertex();
						
						array.position(tx + hx + 0.5F, r.getHeight(tx2 * 2 + x2 + 1, tz2 * 2 + z2    ), tz + hz       );
						Vector3f v4 = normals[(mx + 1) * 33 + mz];
						array.normal(v4.x, v4.y, v4.z);
						array.endVertex();
					}
				}
			}
		}
	}
	
	static Vector3f[] normalsArr = new Vector3f[33 * 33];
	static Vector3f n = new Vector3f();
	static Vector3f temp = new Vector3f();
	static Vector3f v1 = new Vector3f();
	static Vector3f v2 = new Vector3f();
	static Vector3f v3 = new Vector3f();
	
	static {
		for(int i = 0; i < normalsArr.length; i++) {
			normalsArr[i] = new Vector3f();
		}
	}
	
	private static Vector3f[] findNormals(Region r) {
		int i = 0;
		
		for(int x = 2; x < 35; x++) {
			for(int z = 2; z < 35; z++) {
				n.set(0, 0, 0);
				temp.set(0, 0, 0);
				
				v1.set(x, r.getHeight(x, z), z);
				
				v2.set(x, r.getHeight(x, z + 1), z + 1);
				v3.set(x + 1, r.getHeight(x + 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				n.add(temp);
				
				v2.set(x + 1, r.getHeight(x + 1, z), z);
				v3.set(x, r.getHeight(x, z - 1), z - 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				n.add(temp);
				
				v2.set(x, r.getHeight(x, z - 1), z - 1);
				v3.set(x - 1, r.getHeight(x - 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				n.add(temp);
				
				v2.set(x - 1, r.getHeight(x - 1, z), z);
				v3.set(x, r.getHeight(x, z + 1), z + 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				n.add(temp);
				
				n.normalize();
				
				normalsArr[i].set(n.x, n.y, n.z);
				i++;
			}
		}
		return normalsArr;
	}

	@Override
	public void onAdded(Region r) {
		PlatformClient.runInRenderThread(new Runnable() {
			
			@Override
			public void run() {
				regions.put(r.coord, r);
			}
		});
	}

	@Override
	public void onRemoved(Region r) {
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
