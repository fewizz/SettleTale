package ru.settletale.client.render.world;

import static org.lwjgl.opengl.GL11.*;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.client.Client;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.gl.VertexArray;
import ru.settletale.client.render.Color;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.FontRenderer;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.render.util.GLUtils;
import ru.settletale.client.resource.Textures;
import ru.settletale.world.region.IChunkManagerListener;
import ru.settletale.world.region.Chunk;

public class WorldRenderer implements IChunkManagerListener {
	public static final WorldRenderer INSTANCE = new WorldRenderer();
	public static final HashLongObjMap<CompiledRegion> REGIONS_TO_RENDER = HashLongObjMaps.newMutableMap();
	static final ShaderProgram PROGRAM_SKY = new ShaderProgram();
	static Texture2D textureGrass;
	static Texture2D textureTom;
	//static TextureAtlas atlas;

	public static void init() {
		glColor4f(1, 1, 1, 1);
		glClearColor(0.1F, 0F, 0F, 1F);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_ALPHA_TEST);
		GLUtils.linkShadersToProgram(PROGRAM_SKY, "shaders/sky.vs", "shaders/sky.fs");
		
		textureGrass = Textures.getOrLoad("textures/grass.png");
		textureTom = Textures.getOrLoad("textures/tom.png");
		/*atlas = new TextureAtlas(1024, 1024);
		atlas.gen();
		atlas.append(textureGrass);
		//atlas.append(textureTom);*/
	}

	public static void render() {
		Renderer.debugGL("World rend start");
		
		Renderer.PROJ_MATRIX.setPerspective((float) Math.toRadians(95F), (float) Client.WINDOW.getWidth() / (float) Client.WINDOW.getHeight(), 0.1F, 1000F);
		
		Renderer.VIEW_MATRIX.identity();
		Renderer.VIEW_MATRIX.rotateDeg(-Camera.rotationX, 1F, 0, 0);
		Renderer.VIEW_MATRIX.rotateDeg(-Camera.rotationY, 0, 1F, 0);
		Renderer.VIEW_MATRIX.translate(-Camera.POSITION.x, -Camera.POSITION.y, -Camera.POSITION.z);
		
		Renderer.debugGL("First translates");

		Renderer.updateCombinedMatrixUniformBlock();

		Renderer.debugGL("World rend after transforms");

		glEnable(GL_CULL_FACE);

		REGIONS_TO_RENDER.forEach((long l, CompiledRegion r) -> {
			r.render();
		});

		glDisable(GL_CULL_FACE);

		Renderer.updateInversedMatricesUniformBlock();
		VertexArray.DEFAULT.bind();
		PROGRAM_SKY.use();
		glDrawArrays(GL_QUADS, 0, 4);
		Renderer.debugGL("Render sky end");

		//Renderer.VIEW_MATRIX.push();
		//Renderer.VIEW_MATRIX.translate(0, 100, 0);
		//Renderer.VIEW_MATRIX.scale(5F);
		//GL.updateMatriciesUniformBlock();
		//Renderer.updateCombinedMatrixUniformBlock();
		//ObjModelLoader.MODELS.get("models/dabrovic/sponza.obj").render();
		//ColladaLoader.MODELS.get("models/collada/Glock_3d.dae").render();
		//ColladaLoader.MODELS.get("models/collada/green.dae").render();
		//Renderer.VIEW_MATRIX.pop();
		
		Renderer.updateCombinedMatrixUniformBlock();
		
		glLineWidth(10);
		Drawer.begin(GL_LINES);
		Drawer.COLOR.set(Color.RED);
		Drawer.vertex(0, 50, 0);
		Drawer.vertex(100, 50, 0);
		Drawer.draw();

		Drawer.begin(GL_QUADS);
		Drawer.COLOR.set(Color.WHITE);
		Drawer.texture(textureGrass);
		Drawer.UV.set(0, 0);
		Drawer.vertex(0, 50, 0);
		Drawer.UV.set(0, 1);
		Drawer.vertex(0, 100, 0);
		Drawer.UV.set(1, 1);
		Drawer.vertex(50, 100, 0);
		Drawer.UV.set(1, 0);
		Drawer.vertex(50, 50, 0);

		Drawer.texture(textureTom);
		Drawer.UV.set(0, 1);
		Drawer.vertex(50, 50, 0);
		Drawer.UV.set(0, 0);
		Drawer.vertex(50, 100, 0);
		Drawer.UV.set(1, 0);
		Drawer.vertex(150, 100, 0);
		Drawer.UV.set(1, 1);
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

		Renderer.PROJ_MATRIX.identity();
		Renderer.PROJ_MATRIX.ortho2D(0, Client.WINDOW.getWidth(), 0, Client.WINDOW.getHeight());
		Renderer.VIEW_MATRIX.identity();
		Renderer.updateCombinedMatrixUniformBlock();

		FontRenderer.setSize(25);
		FontRenderer.setColor(Color.WHITE);
		FontRenderer.getPosition().set(10, Client.WINDOW.getHeight() - 30, 0);
		FontRenderer.setText("FPS: " + Renderer.lastFPS);
		FontRenderer.render();
	}

	@Override
	public void onChunkAdded(Chunk r) {
		r.increaseThreadUsage();
		
		Client.GL_THREAD.addTask(() -> {
			CompiledRegion cr = new CompiledRegion(r);
			Renderer.debugGL("Fill buffers");
			cr.compile();
			Renderer.debugGL("Array clear");
			
			REGIONS_TO_RENDER.put(r.coordClamped, cr);
		});
	}

	@Override
	public void onChunkRemoved(Chunk r) {
		Client.GL_THREAD.addTask(() -> {
			if (REGIONS_TO_RENDER.containsKey(r.coordClamped)) {
				REGIONS_TO_RENDER.get(r.coordClamped).clear();
				REGIONS_TO_RENDER.remove(r.coordClamped);
				r.decreaseThreadUsage();
			}
		});

	}
}
