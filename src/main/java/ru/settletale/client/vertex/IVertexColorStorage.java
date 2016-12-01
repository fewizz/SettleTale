package ru.settletale.client.vertex;

public interface IVertexColorStorage extends IVertexStorage {
	void color(byte r, byte g, byte b, byte a, int index);
}
