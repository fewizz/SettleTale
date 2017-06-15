package ru.settletale.entity;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;

import ru.settletale.SettleTale;
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
	public Vector3d lastInter = new Vector3d();
	public IntersectionResult camInter = new IntersectionResult();

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
		if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
			//V.y += 4;
			keyborardSpeed.y += 1;
			V.set(0);
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			keyborardSpeed.y -= 1;
			V.set(0);
		}

		//V.add(a.div(20F, new Vector3f()));

		Vector3dp newPos = new Vector3dp(position);
		keyborardSpeed.mul(2);
		newPos.add(V).add(keyborardSpeed);
		newPos.previous.set(position);

		//IntersectionResult coll = checkCollision(newPos);
		/*while(coll != null) {
			newPos.previous.set(newPos);
			newPos.set(coll);
			newPos.add(coll.normal.mul(0.1F));
			coll = checkCollision(newPos);
			V.set(0);
		}*/
		/*if(coll != null) {
			newPos.set(coll);
			newPos.add(coll.normal.mul(0.1F));
			V.set(0);
		}*/
		//else {
		//	System.out.println("NULL");
		//}
		position.updatePrevious();
		position.set(newPos);
		/*position.updatePrevious();
		position.add(keyborardSpeed);

		Vector4d camVec = new Vector4d(0, 0, -300, 1);

		Matrix4d mat = new Matrix4d();
		mat.translate((float) position.x, (float) position.y, (float) position.z);
		mat.rotate(Math.toRadians(-rotationY), 0, 1, 0);
		mat.rotate(Math.toRadians(-rotationX), 1, 0, 0);

		camVec.mul(mat);
		Vector3dp camVec2 = new Vector3dp();
		camVec2.set(camVec.x, camVec.y, camVec.z);
		camVec2.previous.set(position);

		camInter = checkCollision(camVec2);
		if (camInter == null) {
			System.out.println("null");
		}*/
	}

	public IntersectionResult checkCollision(Vector3dp pos) {
		Vector3d dir = pos.sub(pos.previous, new Vector3d());

		int stepCount = MathUtils.ceil(Math.abs(dir.x)) * 2 + MathUtils.ceil(Math.abs(dir.z)) * 2;

		stepCount = Math.max(stepCount, 1);

		Vector2i pixelOffset = new Vector2i(MathUtils.floor(pos.previous.x), MathUtils.floor(pos.previous.z));
		Vector3d dirNormal = dir.normalize(new Vector3d());
		Vector2i cellStep = new Vector2i();
		cellStep.x = dirNormal.x == 0 ? 0 : dirNormal.x > 0 ? 1 : -1;
		cellStep.y = dirNormal.z == 0 ? 0 : dirNormal.z > 0 ? 1 : -1;

		Vector3d v0 = new Vector3d();
		Vector3d v1 = new Vector3d();
		Vector3d v2 = new Vector3d();
		Vector3d v3 = new Vector3d();
		Segment segment = new Segment(new Vector3d(), dir);
		segment.p1.sub(dirNormal.mul(0.05F), new Vector3d());
		segment.p2.add(dirNormal.mul(0.05F), new Vector3d());
		
		Segment segmentForPixelCheck = new Segment(pos.previous, pos);
		segmentForPixelCheck.p1.y = 0;
		segmentForPixelCheck.p2.y = 0;

		for (int step = 0; step < stepCount; step++) {
			if (step != 0) {
				double lenX = Distance.segmentPoint(segmentForPixelCheck, (double)(pixelOffset.x + cellStep.x) + 0.5D, 0, (double)pixelOffset.y + 0.5D);
				double lenZ = Distance.segmentPoint(segmentForPixelCheck, (double)pixelOffset.x + 0.5D, 0, (double)(pixelOffset.y + cellStep.y) + 0.5D);

				if (lenX < lenZ) {
					pixelOffset.add(cellStep.x, 0);
				}
				else {
					pixelOffset.add(0, cellStep.y);
				}
			}

			double offX = (float) pixelOffset.x;
			double offZ = (float) pixelOffset.y;

			Region r = SettleTale.getWorld().getRegion(MathUtils.floor(offX / Region.WIDTH_F), MathUtils.floor(offZ / Region.WIDTH_F));
			if (r == null) {
				continue;
			}

			int xi = MathUtils.floor(MathUtils.fract(offX / Region.WIDTH_F) * Region.WIDTH_F);
			int zi = MathUtils.floor(MathUtils.fract(offZ / Region.WIDTH_F) * Region.WIDTH_F);

			double h1 = r.getHeight(xi, zi);
			double h2 = r.getHeight(xi, zi + 1);
			double h3 = r.getHeight(xi + 1, zi + 1);
			double h4 = r.getHeight(xi + 1, zi);

			v0.set(offX, h1, offZ).sub(pos.previous);
			v1.set(offX, h2, offZ + 1D).sub(pos.previous);
			v2.set(offX + 1D, h3, offZ + 1D).sub(pos.previous);
			v3.set(offX + 1D, h4, offZ).sub(pos.previous);

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

			double len1 = Double.MAX_VALUE;
			IntersectionResult result = null;

			if (ir1.succes && ir1.x >= v0.x && ir1.x <= v2.x && ir1.z >= v0.z && ir1.z <= v2.z) {
				len1 = ir1.length();
				result = ir1;
				result.normal = normal1;
			}
			if (ir2.succes && ir2.x >= v0.x && ir2.x <= v2.x && ir2.z >= v0.z && ir2.z <= v2.z) {
				double len2 = ir2.length();
				if (len2 < len1) {
					result = ir2;
					result.normal = normal2;
				}
			}
			if (result == null) {
				continue;
			}

			result.add(pos.previous);
			return result;
		}

		return null;
	}

}