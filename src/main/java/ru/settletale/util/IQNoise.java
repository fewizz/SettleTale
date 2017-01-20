package ru.settletale.util;

import ru.settletale.world.layer.LayerAbstract;

public class IQNoise {

	public static void main(String[] args) {
		float max = 0;
		
		for (float x = 0; x < 20000; x++) {
			float i = nosie(x, 0);
			if(i > max) {
				max = i;
			}
		}
		
		System.out.println(max);
	}

	public static float nosie(float xp, float yp) {
		float cx = (float) Math.floor(xp);
		float cy = (float) Math.floor(yp);
		
		float cxo = SSMath.fract(xp);
		float cyo = SSMath.fract(yp);

		float value = 0;
		float accum = 0;

		for (float x = -1; x <= 1; x++)
			for (float y = -1; y <= 1; y++) {
				float px = x - cxo;
				float py = y - cyo;

				float centerDistance = px * px + py * py;

				float sample = 1.0F - SSMath.smoothstep(0.0F, 1F, centerDistance);

				float color = rand(px + cx, py + cy);
				value += color * sample;
				accum += sample;
			}

		return value / accum;
	}

	public static float rand(float x, float y) {
		return LayerAbstract.getPRInt((int)x, (int)y, 200000) / 100000F - 1F;//(float) SSMath.fract(SSMath.dot(x * (x / 5F) * 34.3453F, y * 57.43634F, 14.2453425F * (x + 8.3453F), 81.9452345F * (y + 8.4354F)) * 20.56348356F) * 2F - 1F;
	}

}
