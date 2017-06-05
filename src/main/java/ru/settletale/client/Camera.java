package ru.settletale.client;

import org.joml.Vector3d;

import ru.settletale.SettleTaleStart;
import ru.settletale.client.render.Renderer;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;

public class Camera {
	public static Vector3d position = new Vector3d();
	public static float rotationX = 90;
	public static float rotationY;

	public static void update() {
		EntityPlayer player = GameClient.player;
		
		TickTimer t = SettleTaleStart.getWorld().updateThread.timer;
		double d = (Renderer.TIMER.startTimeNano - t.startTimeNano) / (float)(t.waitTimeNano);
		
		d = Math.min(d, 1);
		
		MathUtils.interpolate(player.position.previous, player.position, position, d);
		
		rotationX += -Cursor.POSITION.y / 3F;
		rotationY += Cursor.POSITION.x / 3F;
		
		rotationX = MathUtils.clamp(90, -90, rotationX);
	}
}
