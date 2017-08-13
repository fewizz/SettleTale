package ru.settletale.phys;

import java.util.List;

import org.joml.Vector3d;

import ru.settletale.math.Triangle;

public class TriangleMesh extends CollidableObject {
	List<Triangle> triangles;
	
	public TriangleMesh(List<Triangle> triangles) {
		this.triangles = triangles;
	}
	
	@Override
	void generateAABB() {
		Vector3d min = new Vector3d();
		Vector3d max = new Vector3d();
		
		triangles.forEach(tr -> {
			tr.forEachPoint(point -> {
				for(int comp = 0; comp < 3; comp++) {
					double val = point.get(comp);
					
					if(val < min.get(comp)) {
						min.setComponent(comp, val);
					}
					
					if(val > max.get(comp)) {
						max.setComponent(comp, val);
					}
				}
			});
		});
		
		Vector3d dim = new Vector3d(max.sub(min, new Vector3d()));
		Vector3d pos = new Vector3d();
		pos.add(min);
		pos.add(dim.div(2), new Vector3d());
		
		this.aabb = new AABB(pos, dim);
	}
	
}
