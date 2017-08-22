package ru.settletale.client;

import org.joml.Vector3f;

import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;

public class Camera {
	public static final Vector3f POSITION = new Vector3f();
	static final Vector3f TEMP = new Vector3f();
	public static float rotationX = 0;
	public static float rotationY = 0;

	public static void update() {
		EntityPlayer player = Client.player;
		
		TickTimer t = Client.world.tickTimer;
		long timeNano = System.nanoTime();
		
		float d = (float)(timeNano - t.startTimeNano) / (float)(t.lastSpendTimeNano);
		
		d = Math.min(d, 1F);
		
		POSITION.set(player.position.previous);
		TEMP.set(player.position);
		POSITION.lerp(TEMP, (float) d);
		
		rotationX -= (Client.WINDOW.getCursorY() - Client.WINDOW.getHeight() / 2D) / 3F;
		rotationY -= (Client.WINDOW.getCursorX() - Client.WINDOW.getWidth() / 2D) / 3F;
		rotationX = MathUtils.clamp(90, -90, rotationX);
	}
}
