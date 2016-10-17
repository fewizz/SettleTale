package ru.settletale.world;

import ru.settletale.util.SSMath;

public class LayerScaleX2Random extends Layer {

	public LayerScaleX2Random(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		boolean evenX = x % 2 == 0;
		boolean evenZ = z % 2 == 0;
		//boolean evenW = width % 2 == 0;
		//boolean evenL = length % 2 == 0;
		//boolean evenEver = evenX && evenZ && evenW && evenL;
		int widthParent = (width / 2) + 3/*(evenEver ? 2 : 3)*/;
		int lengthParent = (length / 2) + 3/*(evenEver ? 2 : 3)*/;
		int xParent = SSMath.floor((float)x / 2F) + (x < 0 ? (evenX ? 0 : -1) : -1);
		int zParent = SSMath.floor((float)z / 2F) + (z < 0 ? (evenZ ? 0 : -1) : -1);
		byte[] valuesParent = parent.getValues(xParent, zParent, widthParent, lengthParent);
		byte[] values = getByteArray(width, length);//new byte[width * length];
		
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
			
			for(int x2 = 0; x2 < length; x2++) {
				
				index++;
				upIDP += x2 % 2 != 0 ? weven : wnven;
				downIDP += x2 % 2 != 0 ? weven : wnven;
				leftIDP += x2 % 2 != 0 ? weven : wnven;
				rightIDP += x2 % 2 != 0 ? weven : wnven;
				centIDP += x2 % 2 != 0 ? weven : wnven;
				
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
