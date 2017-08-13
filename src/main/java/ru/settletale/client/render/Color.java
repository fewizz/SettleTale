package ru.settletale.client.render;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Color {
	public static final ColorImmutable RED = new ColorImmutable(0xFF, 0x00, 0x00, 0xFF);
	public static final ColorImmutable GREEN = new ColorImmutable(0x00, 0xFF, 0x00, 0xFF);
	public static final ColorImmutable BLUE = new ColorImmutable(0x00, 0x00, 0xFF, 0xFF);
	public static final ColorImmutable WHITE = new ColorImmutable(0xFF, 0xFF, 0xFF, 0xFF);
	public static final ColorImmutable BLACK = new ColorImmutable(0x00, 0x00, 0x00, 0xFF);

	int hex;

	public Color() {
		//r = g = b = a = (byte) 0xFF;
		hex = 0xFFFFFFFF;
	}

	public Color(int r, int g, int b, int a) {
		set((byte) r, (byte) g, (byte) b, (byte) a);
	}

	public Color(Color c) {
		set(c);
	}

	public Color(float r, float g, float b, float a) {
		this.set(r, g, b, a);
	}

	public Color(byte r, byte g, byte b, byte a) {
		this.set(r, g, b, a);
	}

	public void set(float r, float g, float b, float a) {
		this.set((byte) (r * 255), (byte) (g * 255), (byte) (b * 255), (byte) (a * 255));
	}

	public void set(byte r, byte g, byte b, byte a) {
		/*this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;*/
		hex = 0;
		hex |= a & 0xFF;
		hex |= ((int)b << 8) & 0xFF00;
		hex |= ((int)g << 16) & 0xFF0000;
		hex |= ((int)r << 24) & 0xFF000000;
	}
	
	public void set(int color) {
		this.hex = color;
	}

	public void set(Color c) {
		this.set(c.hex);
	}

	public byte r() {
		return (byte) (this.hex >>> 24);
	}

	public byte g() {
		return (byte) ((this.hex >>> 16) & 0xFF);
	}

	public byte b() {
		return (byte) ((this.hex >>> 8) & 0xFF);
	}

	public byte a() {
		return (byte) (hex & 0xFF);
	}

	public float fr() {
		return (float) this.r() / 255F;
	}

	public float fg() {
		return (float) this.g() / 255F;
	}

	public float fb() {
		return (float) this.b() / 255F;
	}

	public float fa() {
		return (float) this.a() / 255F;
	}
	
	public int hex() {
		return hex;
	}

	public Vector3f get(Vector3f v3f) {
		v3f.set(fr(), fg(), fb());
		return v3f;
	}
	
	public Vector4f get(Vector4f v4f) {
		v4f.set(fr(), fg(), fb(), fa());
		return v4f;
	}
	
	public Vector4f getAsNewVector4f() {
		return new Vector4f(fr(), fg(), fb(), fa());
	}
	
	public Vector3f getAsNewVector3f() {
		return new Vector3f(fr(), fg(), fb());
	}

	public static class ColorImmutable extends Color {

		public ColorImmutable(int r, int g, int b, int a) {
			super.set((byte) r, (byte) g, (byte) b, (byte) a);
			System.out.println(Integer.toHexString(hex));
		}

		@Override
		public void set(byte r, byte g, byte b, byte a) {
			// nope
		}
		
		@Override
		public void set(int c) {
			// nope
		}
	}
}
