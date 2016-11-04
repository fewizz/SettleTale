package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.nio.ByteBuffer;

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
				cr = new CompiledRegion(r);
				cr.compile(r, pb, cb);
				OpenGL.debug("Array clear");
				pa.clear();
				regionsToRender.put(r.coord, cr);
			}
			cr.render();
		}

		OpenGL.viewMatrix.pop();
		OpenGL.debug("World rend end");
	}
	
	public static void renderRegion(Region r, PrimitiveArray array) {
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
						float hx = (float) x2 / 2F;
						float hz = (float) z2 / 2F;
						
						array.position(tx + hx       , r.getHeight(tx2 * 2 + x2    , tz2 * 2 + z2    ), tz + hz       );
						array.endVertex();
						array.position(tx + hx       , r.getHeight(tx2 * 2 + x2    , tz2 * 2 + z2 + 1), tz + hz + 0.5F);
						array.endVertex();
						array.position(tx + hx + 0.5F, r.getHeight(tx2 * 2 + x2 + 1, tz2 * 2 + z2 + 1), tz + hz + 0.5F);
						array.endVertex();
						array.position(tx + hx + 0.5F, r.getHeight(tx2 * 2 + x2 + 1, tz2 * 2 + z2    ), tz + hz       );
						array.endVertex();
					}
				}
			}
		}
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
