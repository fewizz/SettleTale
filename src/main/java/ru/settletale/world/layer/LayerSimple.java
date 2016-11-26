package ru.settletale.world.layer;

public class LayerSimple extends Layer {

	public LayerSimple() {
		super(null);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		int size = width * length;
		byte[] values = new byte[size];
		
		//int btlen = BiomeType.values().length + 1;
		
		for(int i = 0; i < size; i++) {
			//int rint = getPRInt(x + (i % width), z + (i / length), btlen);
			//if(rint <= 1) {
			//	values[i] = (byte) BiomeType.SEA.ordinal();
			//}
			//else {
			//	values[i] = (byte) (rint - 1);
			//}
		}
		
		return values;
	}

}
