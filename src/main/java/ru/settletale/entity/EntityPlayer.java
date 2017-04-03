package ru.settletale.entity;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import ru.settletale.Game;
import ru.settletale.client.Camera;
import ru.settletale.client.KeyListener;
import ru.settletale.util.MathUtils;
import ru.settletale.util.Vector3fp;
import ru.settletale.world.region.Region;

public class EntityPlayer extends Entity {

	Vector3f a = new Vector3f(0, -9.8F, 0);
	Vector3f V = new Vector3f();
	
	@Override
	public void update() {
		Vector3f keyborardSpeed = new Vector3f(0);
		rotationX = Camera.rotationX;
		rotationY = Camera.rotationY;

		if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
			keyborardSpeed.z += (float) -Math.cos(Math.toRadians(rotationY));
			keyborardSpeed.x += (float) Math.sin(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
			keyborardSpeed.z += (float) Math.cos(Math.toRadians(rotationY));
			keyborardSpeed.x += (float) -Math.sin(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
			keyborardSpeed.z += (float) Math.sin(Math.toRadians(rotationY));
			keyborardSpeed.x += (float) Math.cos(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
			keyborardSpeed.z += (float) -Math.sin(Math.toRadians(rotationY));
			keyborardSpeed.x += (float) -Math.cos(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressedFirst(GLFW_KEY_SPACE)) {
			V.y += 4;
		}

		V.add(a.div(20F, new Vector3f()));
		
		Vector3fp newPos = new Vector3fp(position);
		
		keyborardSpeed.mul(10);
		newPos.add(V).add(keyborardSpeed);
		newPos.previous.set(position);
		checkCollision(newPos);
		
		position.set(newPos);
	}

	public void checkCollision(Vector3fp newPos) {
		Vector3f dist = newPos.sub(newPos.previous, new Vector3f());

		float stepSize;
		if(dist.x == 0 && dist.z == 0) {
			stepSize = Math.abs(dist.y);
		}
		else {
			stepSize = (dist.length() / Math.max(Math.abs(dist.x), Math.abs(dist.z))) / 2F;
		}
		
		int stepCount = (int) Math.ceil(dist.length() / stepSize);
		stepSize = dist.length() / (float) stepCount;

		Vector3f step = dist.div(stepCount, new Vector3f());
		Vector3f current = new Vector3f(newPos.previous).add(step);

		Vector3f v0 = new Vector3f();
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f v3 = new Vector3f();

		for (int i = 0; i < stepCount; i++) {
			Region r = Game.getWorld().getRegion(MathUtils.floor(current.x / Region.WIDTH_F), MathUtils.floor(current.z / Region.WIDTH_F));
			if(r == null) {
				continue;
			}
			
			float x = MathUtils.floor(current.x * 2F) / 2F;
			float z = MathUtils.floor(current.z * 2F) / 2F;
			int xi = MathUtils.floor(MathUtils.fract((current.x) / Region.WIDTH_F) * (Region.WIDTH_F * 2F));
			int zi = MathUtils.floor(MathUtils.fract((current.z) / Region.WIDTH_F) * (Region.WIDTH_F * 2F));
			
			float h1 = r.getHeight(xi, zi);
			float h2 = r.getHeight(xi, zi + 1);
			float h3 = r.getHeight(xi + 1, zi + 1);
			float h4 = r.getHeight(xi + 1, zi);
			
			v0.set(x, h1, z);
			v1.set(x, h2, z + 0.5F);
			v2.set(x + 0.5F, h3, z + 0.5F);
			v3.set(x + 0.5F, h4, z);
			
			v0.lerp(v3, MathUtils.fract(current.x * 2F));
			v0.lerp(v1, MathUtils.fract(current.z * 2F));
			if(current.y - 1F <= v0.y) {
				newPos.set(newPos.x, v0.y + 1F, newPos.z);
				V.set(0);
				return;
			}
			
			v1.lerp(v2, MathUtils.fract(current.x * 2F));
			v3.set(v1);
			v3.lerp(v2, MathUtils.fract(current.z * 2F));
			if(current.y - 1F <= v3.y) {
				newPos.set(newPos.x, v3.y + 1F, newPos.z);
				V.set(0);
				return;
			}
			
			current.add(step);
		}
	}

}