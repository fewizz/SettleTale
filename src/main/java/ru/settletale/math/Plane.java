package ru.settletale.math;

import org.joml.Vector3f;

public class Plane {
	Vector3f origin;
	Vector3f normal;

	public Plane(Vector3f origin, Vector3f normal) {
		this.origin = origin;
		this.normal = normal.normalize();
	}
	
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f temp) {
		this.origin = p1;
		this.normal = new Vector3f();
		p1.sub(p2, normal);
		p1.sub(p3, temp);
		normal.cross(temp, normal);
		normal.normalize();
	}

	public float getY(float x, float z) {
		/** 
		v = (x, y, z)
		dist = o - v
		dot(o - v, normal) / length(o - v) = dot(o - v, normal) = dot(dist, normal) = 0
		dist.x*normal.x + dist.y*normal.y + dist.z*normal.z = 0
		
		dist.y*normal.y = -dist.z*normal.z - dist.x*normal.x
		dist.y = (-dist.z*normal.z - dist.x*normal.x) / normal.y
		y - y = (-(o.z - z)*normal.z - (o.x - x)*normal.x) / normal.y
		-y = ((-(o.z - z)*normal.z - (o.x - x)*normal.x) / normal.y) - o.y
		y = o.y - ((-(o.z - z)*normal.z - (o.x - x)*normal.x) / normal.y)
		y = o.y - (((z - o.z)*normal.z - (o.x - x)*normal.x) / normal.y)
		*/
		float numerator = ((z - origin.z) * normal.z) - ((origin.x - x) * normal.x);
		float denominator = normal.y;

		if (denominator == 0) {
			return origin.y;
		}

		return origin.y - (numerator / denominator);
	}

	public static void main(String[] args) {
		Plane p = new Plane(new Vector3f(0, 5, 0), new Vector3f(1, 1, 0));

		System.out.println(p.getY(1, 0));
	}
}
