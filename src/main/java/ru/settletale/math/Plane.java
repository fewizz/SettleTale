package ru.settletale.math;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class Plane {
	final Vector3d origin = new Vector3d();
	public final Vector3d normal = new Vector3d();

	public Plane(Vector3d origin, Vector3d normal) {
		this.origin.set(origin);
		this.normal.set(normal).normalize();
	}
	
	public Plane(Vector3d p1, Vector3d p2, Vector3d p3) {
		p1.sub(p2, normal);
		p1.sub(p3, origin);
		normal.cross(origin);
		normal.normalize();
		this.origin.set(p1);
	}
	
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3) {
		Vector3f temp1 = new Vector3f();
		Vector3f temp2 = new Vector3f();
		Vector3f norm = new Vector3f();
		
		p1.sub(p2, temp1);
		p1.sub(p3, temp2);
		temp1.cross(temp2, norm);
		norm.normalize();
		this.normal.set(norm);
		this.origin.set(p1);
	}

	public double getY(double x, double z) {
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
		double numerator = ((z - origin.z) * normal.z) - ((origin.x - x) * normal.x);
		double denominator = normal.y;

		if (denominator == 0) {
			return origin.y;
		}

		return origin.y - (numerator / denominator);
	}

	/*public static void main(String[] args) {
		//Plane p = new Plane(new Vector3f(0, 5, 0), new Vector3f(1, 1, 0));

		System.out.println(p.getY(1, 0));
	}*/
}
