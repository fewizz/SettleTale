package ru.settletale.client.render.tile;

import java.awt.Color;

import ru.settletale.Game;
import ru.settletale.client.opengl.PrimitiveArray;
import ru.settletale.client.opengl.VertexInfo;
import ru.settletale.tile.data.DataContainerTerrain;
import ru.settletale.util.SSMath;
import ru.settletale.world.Region;

public class TileRendererTerrain implements ITileRenderer {
	public static VertexInfo vertex = new VertexInfo();

	@Override
	public void render(int x, int z, PrimitiveArray array) {
		Region reg = Game.getWorld().regionManager.getRegion(SSMath.floor2((float) x / 16F), SSMath.floor2((float) z / 16F));

		Color color = reg.getBiome(x & 0xF, z & 0xF).color;
		DataContainerTerrain data = (DataContainerTerrain) Game.getWorld().getTileData(x, z);
		/** 1 **/
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.0F + x;
		vertex.pY = 0.0F + data.buffer.getFloat(0 * 4);
		vertex.pZ = 0.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.0F + x;
		vertex.pY = data.buffer.getFloat(3 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(4 * 4);
		vertex.pZ = 0.5F +z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(1 * 4);
		vertex.pZ = 0.0F + z;
		array.vertex(vertex);
		
		/** 2 **/
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(1 * 4);
		vertex.pZ = 0.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(4 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 1.0F + x;
		vertex.pY = data.buffer.getFloat(5 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 1.0F + x;
		vertex.pY = data.buffer.getFloat(2 * 4);
		vertex.pZ = 0.0F + z;
		array.vertex(vertex);
		
		/** 3 **/
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.0F + x;
		vertex.pY = data.buffer.getFloat(3 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.0F + x;
		vertex.pY = data.buffer.getFloat(6 * 4);
		vertex.pZ = 1.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(7 * 4);
		vertex.pZ = 1.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(4 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		/** 4 **/
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(4 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 0.5F + x;
		vertex.pY = data.buffer.getFloat(7 * 4);
		vertex.pZ = 1.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 1.0F + x;
		vertex.pY = data.buffer.getFloat(8 * 4);
		vertex.pZ = 1.0F + z;
		array.vertex(vertex);
		
		//vertex = new Vertex();
		vertex.r = (byte) color.getRed();
		vertex.g = (byte) color.getGreen();
		vertex.b = (byte) color.getBlue();
		vertex.pX = 1.0F + x;
		vertex.pY = data.buffer.getFloat(5 * 4);
		vertex.pZ = 0.5F + z;
		array.vertex(vertex);

	}

}
