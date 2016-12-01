package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

public interface IVertexStorage {
	ByteBuffer getBuffer();
	void data(byte b1, byte b2, byte b3, byte b4);
	void data(float f1, float f2, float f3, float f4);
	void dataEnd(int id);
	void clear();
}
