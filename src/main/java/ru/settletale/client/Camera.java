package ru.settletale.client;

import org.joml.Vector3f;

import ru.settletale.Game;
import ru.settletale.client.render.MainRenderer;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;

public class Camera {
	public static Vector3f position = new Vector3f();
	public static float rotationX = 90;
	public static float rotationY;

	public static void update() {
		EntityPlayer player = PlatformClient.player;
		
		TickTimer t = Game.getWorld().updateThread.timer;
		float f = (MainRenderer.TIMER.startTimeNano - t.startTimeNano) / (float)(t.waitTimeNano);
		
		f = Math.min(f, 1);
		
		MathUtils.interpolate(player.position.previous, player.position, position, f);
		
		rotationX += -Cursor.position.y / 2F;
		rotationY += Cursor.position.x / 2F;
		
		if(rotationX > 90) {
			rotationX = 90;
		}
		if(rotationX < -90) {
			rotationX = -90;
		}
	}
}
