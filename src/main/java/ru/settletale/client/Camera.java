package ru.settletale.client;

import org.joml.Vector3d;

import ru.settletale.Game;
import ru.settletale.client.render.MainRenderer;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;

public class Camera {
	public static Vector3d position = new Vector3d();
	public static float rotationX = 90;
	public static float rotationY;

	public static void update() {
		EntityPlayer player = PlatformClient.player;
		
		TickTimer t = Game.getWorld().updateThread.timer;
		double d = (MainRenderer.TIMER.startTimeNano - t.startTimeNano) / (float)(t.waitTimeNano);
		
		d = Math.min(d, 1);
		
		MathUtils.interpolate(player.position.previous, player.position, position, d);
		
		rotationX += -Cursor.position.y / 3F;
		rotationY += Cursor.position.x / 3F;
		
		rotationX = MathUtils.clamp(90, -90, rotationX);
	}
}
