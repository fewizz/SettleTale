package ru.settletale.world;

public class LayerScaleX2 extends Layer {

	public LayerScaleX2(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		int parentWidth = (width / 2) + 2;
		int parentLength = (length / 2) + 2;
		byte[] parentValues = parent.getValues(x - 1, z - 1, parentWidth, parentLength);
		byte[] values = getByteArray(width, length);//new byte[width * length];
		
		for(int x2 = 0; x2 < width / 2; x2++) {
			for(int z2 = 0; z2 < length / 2; z2++) {
				int value = parentValues[(z2 + 1) * parentLength + (x2 + 1)];
				
				int rightUp = ((z2 * 2) + 1) * length + ((x2 * 2) + 1);
				int rightDown = (z2 * 2) * length + ((x2 * 2) + 1);
				int leftUp = ((z2 * 2) + 1) * length + (x2 * 2);
				int leftDown = (z2 * 2) * length + (x2 * 2);
				
				values[rightUp] = (byte) value;
				values[rightDown] = (byte) value;
				values[leftUp] = (byte) value;
				values[leftDown] = (byte) value;
			}
		}
		
		return values;
	}
	
}
