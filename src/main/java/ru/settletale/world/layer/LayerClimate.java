package ru.settletale.world.layer;

public class LayerClimate extends Layer {

	public LayerClimate() {
		super(null);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		byte[] values = getByteArray(width, length);//new byte[width * length];

		for (int x2 = 0; x2 < width; x2++) {
			for (int z2 = 0; z2 < length; z2++) {
				int i = Math.abs((x + x2) - (z + z2));

				i += noise.eval((float) (x + x2) / 2.5F, (float) (z + z2) / 2.5F) * 2.5F;
				
				i += 1;

				int type = (int) ((float) i / 2.5F);
				if (type >= 5) {
					type = 4;
				}
				if (type < 0) {
					type = 0;
				}
				values[z2 * length + x2] = (byte) type;
			}
		}

		return values;
	}

}
