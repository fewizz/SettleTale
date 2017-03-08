package ru.settletale.client.render;

public class Color {
	public static final ColorImmutable RED = new ColorImmutable(0xFF, 0, 0, 0xFF);
	public static final ColorImmutable GREEN = new ColorImmutable(0, 0xFF, 0, 0xFF);
	public static final ColorImmutable BLUE = new ColorImmutable(0, 0, 0xFF, 0xFF);
	public static final ColorImmutable WHITE = new ColorImmutable(0xFF, 0xFF, 0xFF, 0xFF);
	public static final ColorImmutable BLACK = new ColorImmutable(0, 0, 0, 0xFF);
	
	private byte r;
	private byte g;
	private byte b;
	private byte a;
	
	public Color() {
		r = g = b = a = (byte) 0xFF;
	}
	
	public Color(int r, int g, int b, int a) {
		set((byte)r, (byte)g, (byte)b, (byte)a);
	}
	
	public Color(float r, float g, float b, float a) {
		this.set(r, g, b, a);
	}
	
	public Color(byte r, byte g, byte b, byte a) {
		this.set(r, g, b, a);
	}
	
	public void set(float r, float g, float b, float a) {
		this.set((byte)(r * 255), (byte)(g * 255), (byte)(b * 255), (byte)(a * 255));
	}
	
	public void set(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void set(Color c) {
		this.set(c.r, c.g, c.b, c.a);
	}
	
	public byte r() {
		return this.r;
	}
	
	public byte g() {
		return this.g;
	}
	
	public byte b() {
		return this.b;
	}
	
	public byte a() {
		return this.a;
	}
	
	public static class ColorImmutable extends Color {
		
		public ColorImmutable(int r, int g, int b, int a) {
			super.set((byte)r, (byte)g, (byte)b, (byte)a);
		}
		
		@Override
		public void set(byte r, byte g, byte b, byte a) {
			// nope
		}
	}
}
