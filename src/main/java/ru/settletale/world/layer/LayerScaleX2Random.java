package ru.settletale.world.layer;

import ru.settletale.util.SSMath;

public class LayerScaleX2Random extends Layer {

	public LayerScaleX2Random(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		boolean evenX = x % 2 == 0;
		boolean evenZ = z % 2 == 0;
		int widthParent = (width / 2) + 3;
		int lengthParent = (length / 2) + 3;
		int xParent = SSMath.floor((float)x / 2F) + (x < 0 ? (evenX ? 0 : -1) : -1);
		int zParent = SSMath.floor((float)z / 2F) + (z < 0 ? (evenZ ? 0 : -1) : -1);
		byte[] valuesParent = parent.getValues(xParent, zParent, widthParent, lengthParent);
		byte[] values = getByteArray(width, length);
		
		int index = -1;
		
		int weven = evenX ? 0 : 1;
		int wnven = evenX ? 1 : 0;
		
		for(int z2 = 0; z2 < width; z2++) {
			int ldt = (evenZ ? z2 / 2 : (z2 + 1) / 2);
			
			int upIDP =    (ldt + 2) * lengthParent + weven + 1;
			int downIDP =  (ldt + 0) * lengthParent + weven + 1;
			int leftIDP =  (ldt + 1) * lengthParent + weven + 0;
			int rightIDP = (ldt + 1) * lengthParent + weven + 2;
			int centIDP =  (ldt + 1) * lengthParent + weven + 1;
			upIDP--;
			downIDP--;
			leftIDP--;
			rightIDP--;
			centIDP--;
			
			boolean even = false;
			int toAdd;
			for(int x2 = 0; x2 < length; x2++) {
				index++;
				
				toAdd = even ? weven : wnven;
				
				upIDP += toAdd;
				downIDP += toAdd;
				leftIDP += toAdd;
				rightIDP += toAdd;
				centIDP += toAdd;
				
				int valueUpP = valuesParent[upIDP];
				int valueDownP = valuesParent[downIDP];
				int valueLeftP = valuesParent[leftIDP];
				int valueRightP = valuesParent[rightIDP];
				int valueCentP = valuesParent[centIDP];
				
				int val;
				val = getPRInt(x + x2, z + z2, 6);
				if(val == 0) {
					values[index] = (byte) valueUpP;
				}
				if(val == 1) {
					values[index] = (byte) valueDownP;
				}
				if(val == 2) {
					values[index] = (byte) valueLeftP;
				}
				if(val == 3) {
					values[index] = (byte) valueRightP;
				}
				if(val == 4 || val == 5) {
					values[index] = (byte) valueCentP;
				}
				even = !even;
			}
		}
		
		return values;
	}
	
	public static LayerScaleX2Random getLayer(int count, Layer parent) {
		LayerScaleX2Random layer = new LayerScaleX2Random(parent);
		
		for(int i = 0; i < count - 1; i++) {
			layer = new LayerScaleX2Random(layer);
		}
		
		return layer;
	}
}
