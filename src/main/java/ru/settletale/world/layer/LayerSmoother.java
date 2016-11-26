package ru.settletale.world.layer;

public class LayerSmoother extends Layer {

	public LayerSmoother(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		int parentWidth = width + 2;
		int parentLength = length + 2;
		byte[] valuesParent = parent.getValues(x - 1, z - 1, parentWidth, parentLength);
		byte[] values = getByteArray(width, length);//new byte[width * length];
		
		int index = -1;
		for(int z2 = 1; z2 < parentLength - 1; z2++) {		
			int upIDP =        (z2 + 1) * (parentLength) + 0;
			int upRightIDP =   (z2 + 1) * (parentLength) + 1;
			
			int downIDP =      (z2 - 1) * (parentLength) + 0;
			int downLeftIDP =  (z2 - 1) * (parentLength) - 1;
			
			int leftIDP =      (z2 * parentLength) - 1;
			int leftUpIDP =    ((z2 + 1) * parentLength) + - 1;
			
			int rightIDP =     (z2 * parentLength) + 1;
			int rightDownIDP = ((z2 - 1) * parentLength) + 1;
			
			for(int x2 = 1; x2 < parentWidth - 1; x2++) {
				index++;
				
				upIDP++;
				upRightIDP++;
				
				downIDP++;
				downLeftIDP++;
				
				rightIDP++;
				rightDownIDP++;
				
				leftIDP++;
				leftUpIDP++;
				
				int value = valuesParent[z2 * parentLength + x2];
				
				int up = valuesParent[upIDP];
				int upRight = valuesParent[upRightIDP];
				
				int down = valuesParent[downIDP];
				int downLeft = valuesParent[downLeftIDP];
				
				int left = valuesParent[leftIDP];
				int leftUp = valuesParent[leftUpIDP];
				
				int right = valuesParent[rightIDP];
				int rightDown = valuesParent[rightDownIDP];
				
				if(up != value && up == right && up == down && up == left) {
					values[index] = (byte) up;
					continue;
				}
				
				if(up != value && up == right && up == down && (up == upRight || up == rightDown)) {
					values[index] = (byte) up;
					continue;
				}
				if(right != value && right == down && right == left && (right == rightDown || right == downLeft)) {
					values[index] = (byte) right;
					continue;
				}
				if(down != value && down == left && down == up && (down == downLeft || down == leftUp)) {
					values[index] = (byte) down;
					continue;
				}
				if(left != value && left == up && left == right && (left == leftUp || left == upRight)) {
					values[index] = (byte) left;
					continue;
				}
				
				if(right != value && right == down && right == rightDown && right == upRight && right == downLeft) {
					values[index] = (byte) right;
					continue;
				}
				if(down != value && down == left && down == downLeft && down == rightDown && down == leftUp) {
					values[index] = (byte) down;
					continue;
				}
				if(left != value && left == up && left == leftUp && left == downLeft && left == upRight) {
					values[index] = (byte) left;
					continue;
				}
				if(up != value && up == right && up == upRight && up == leftUp && up == rightDown) {
					values[index] = (byte) up;
					continue;
				}
				
				values[index] = (byte) value;
			}
		}
		
		return values;
	}

	public static LayerSmoother getLayer(int countToSmooth, Layer parent) {
		LayerSmoother smoother = new LayerSmoother(parent);
		
		for(int i = 0; i < countToSmooth - 1; i++) {
			smoother = new LayerSmoother(smoother);
		}
		
		return smoother;
	}
}
