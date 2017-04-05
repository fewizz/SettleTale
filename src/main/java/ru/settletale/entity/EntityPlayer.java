package ru.settletale.entity;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import ru.settletale.Game;
import ru.settletale.client.Camera;
import ru.settletale.client.KeyListener;
import ru.settletale.math.Distance;
import ru.settletale.math.Intersection;
import ru.settletale.math.IntersectionResult;
import ru.settletale.math.Plane;
import ru.settletale.math.Segment;
import ru.settletale.util.MathUtils;
import ru.settletale.util.Vector3dp;
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

		Vector3dp newPos = new Vector3dp(position);
		keyborardSpeed.mul(1);
		newPos.add(V).add(keyborardSpeed);
		newPos.previous.set(position);
		
		while(checkCollision(newPos)) {
			position.set(newPos);
		};
		position.set(newPos);
	}

	public boolean checkCollision(Vector3dp newPos) {
		Vector3d dist = new Vector3d(newPos.sub(newPos.previous, new Vector3d()));

		int stepCount;
		if (dist.x == 0 && dist.z == 0) {
			stepCount = 1;
		} else {
			stepCount = (int) (MathUtils.floor(Math.abs(dist.x) * 2F) + MathUtils.floor(Math.abs(dist.z) * 2F)) - 1;
		}

		Vector2f offset = new Vector2f(MathUtils.floor(newPos.previous.x * 2F) / 2F, MathUtils.floor(newPos.previous.z * 2F) / 2F);
		offset.add(0.25F, 0.25F);
		Vector3d temp = dist.normalize(new Vector3d());
		Vector2f cellStep = new Vector2f(MathUtils.ceil(temp.x), MathUtils.ceil(temp.z));
		cellStep.x /= 2F;
		cellStep.y /= 2F;

		Vector3d v0 = new Vector3d();
		Vector3d v1 = new Vector3d();
		Vector3d v2 = new Vector3d();
		Vector3d v3 = new Vector3d();
		Segment segment = new Segment(new Vector3d(), dist);

		for (int step = 0; step < stepCount + 10; step++) {
			if(step != 0) {
				double lenX = Distance.segmentPoint(segment, offset.x + cellStep.x, 0, 0);
				double lenY = Distance.segmentPoint(segment, 0, 0, offset.y + cellStep.y);
				
				if(lenX < lenY) {
					offset.add(cellStep.x, 0);
				}
				else {
					offset.add(0, cellStep.y);
				}
			}
			
			float offX = offset.x;
			float offZ = offset.y;
			
			Region r = Game.getWorld().getRegion(MathUtils.floor(offX / Region.WIDTH_F), MathUtils.floor(offZ / Region.WIDTH_F));
			if (r == null) {
				continue;
			}

			double x = MathUtils.floor(offX * 2F) / 2F;
			double z = MathUtils.floor(offZ * 2F) / 2F;
			int xi = MathUtils.floor(MathUtils.fract(offX / Region.WIDTH_F) * (Region.WIDTH_F * 2F));
			int zi = MathUtils.floor(MathUtils.fract(offZ / Region.WIDTH_F) * (Region.WIDTH_F * 2F));

			double h1 = r.getHeight(xi, zi);
			double h2 = r.getHeight(xi, zi + 1);
			double h3 = r.getHeight(xi + 1, zi + 1);
			double h4 = r.getHeight(xi + 1, zi);

			v0.set(x, h1, z).sub(newPos.previous);
			v1.set(x, h2, z + 0.5D).sub(newPos.previous);
			v2.set(x + 0.5D, h3, z + 0.5D).sub(newPos.previous);
			v3.set(x + 0.5D, h4, z).sub(newPos.previous);

			IntersectionResult ir1 = new IntersectionResult();
			Plane plane = new Plane(v0, v1, v2);
			Intersection.segmentPlane(segment, plane, ir1);
			Vector3d normal1 = plane.normal;

			IntersectionResult ir2 = new IntersectionResult();
			plane = new Plane(v0, v2, v3);
			Intersection.segmentPlane(segment, plane, ir2);
			Vector3d normal2 = plane.normal;

			if (!ir1.succes && !ir2.succes) {
				continue;
			}

 			double len1 = 0;
			Vector3d result = null;
			Vector3d normal = null;

			if (ir1.succes) {
				len1 = ir1.length();
				result = ir1;
				normal = normal1;
			}
			if (ir2.succes) {
				double len2 = ir2.length();
				if (len2 > len1) {
					result = ir2;
					normal = normal2;
				}
			}
			if(result == null || normal == null) {
				continue;
			}

			newPos.set(result).add(newPos.previous).add(normal.mul(0.01F));
			V.set(0);
			return true;
		}
		
		return false;
	}

}