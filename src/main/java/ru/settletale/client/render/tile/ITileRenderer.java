package ru.settletale.client.render.tile;

import ru.settletale.client.opengl.PrimitiveArray;

public interface ITileRenderer {
	public void render(int x, int z, PrimitiveArray pa);
}
