package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

public interface IVertexStorage {
	ByteBuffer getBuffer();
	ByteBuffer getIndexBuffer();
	void index(int index, int id);
	void clear();
}
