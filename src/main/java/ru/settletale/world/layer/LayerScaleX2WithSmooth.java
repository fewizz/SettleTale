package ru.settletale.world.layer;

public class LayerScaleX2WithSmooth extends Layer {
	public LayerScaleX2WithSmooth(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		int parentWidth = (width / 2) + 2;
		int parentLength = (length / 2) + 2;
		byte[] valuesParent = parent.getValues(x - 1, z - 1, parentWidth, parentLength);
		byte[] values = getByteArray(width, length);//new byte[width * length];
		
		for(int x2 = 0; x2 < width / 2; x2++) {
			for(int z2 = 0; z2 < length / 2; z2++) {
				int value = valuesParent[(z2 + 1) * parentLength + (x2 + 1)];
				
				int rightUpID = ((z2 * 2) + 1) * length + ((x2 * 2) + 1);
				int rightDownID = (z2 * 2) * length + ((x2 * 2) + 1);
				int leftUpID = ((z2 * 2) + 1) * length + (x2 * 2);
				int leftDownID = (z2 * 2) * length + (x2 * 2);
				boolean righUpSetted = false;
				boolean righDownSetted = false;
				boolean leftUpSetted = false;
				boolean leftDownSetted = false;
				
				int up = valuesParent[(z2 + 1 + 1) * parentLength + x2 + 1];
				int upRight = valuesParent[(z2 + 1 + 1) * parentLength + x2 + 1 + 1];
				int right = valuesParent[(z2 + 1) * parentLength + x2 + 1 + 1];
				int rightDown = valuesParent[(z2 + 1 - 1) * parentLength + x2 + 1 + 1];
				int down = valuesParent[(z2 + 1 - 1) * parentLength + x2 + 1];
				int downLeft = valuesParent[(z2 + 1 - 1) * parentLength + x2 + 1 - 1];
				int left = valuesParent[(z2 + 1) * parentLength + x2 - 1 + 1];
				int leftUp = valuesParent[(z2 + 1 + 1) * parentLength + x2 - 1 + 1];
				
				if(up != value && up == right && value != upRight/*up == leftUp && up == rightDown*/) {
					values[rightUpID] = (byte) up;
					righUpSetted = true;
				}
				if(right != value && right == down && value != rightDown/*right == upRight && right == downLeft*/) {
					values[rightDownID] = (byte) right;
					righDownSetted = true;
				}
				if(down != value && down == left && value != downLeft/*down == rightDown && down == leftUp*/) {
					values[leftDownID] = (byte) down;
					leftDownSetted = true;
				}
				if(left != value && left == up && value != leftUp/*left == downLeft && left == upRight*/) {
					values[leftUpID] = (byte) left;
					leftUpSetted = true;
				}
				
				if(!righUpSetted)
					values[rightUpID] = (byte) value;
				if(!righDownSetted)
					values[rightDownID] = (byte) value;
				if(!leftUpSetted)
					values[leftUpID] = (byte) value;
				if(!leftDownSetted)
					values[leftDownID] = (byte) value;
			}
		}
		
		return values;
	}
}
