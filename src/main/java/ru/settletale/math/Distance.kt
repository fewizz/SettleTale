package ru.settletale.math

import org.joml.Vector3d
import ru.settletale.util.MathUtils

object Distance {
	@JvmStatic fun point(x: Double, y: Double, z: Double): Double = Math.sqrt(x * x + y * y + z * z)

	@JvmStatic fun pointPoint(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
		val x = x2 - x1
		val y = y2 - y1
		val z = z2 - z1
		return Math.sqrt(x * x + y * y + z * z)
	}

	@JvmStatic fun segmentPoint(segment: Segment, point: Point): Double = segmentPoint(segment, point.origin)

	@JvmStatic fun segmentPoint(segment: Segment, vector: Vector3d): Double = segmentPoint(segment, vector.x, vector.y, vector.z)

	@JvmStatic fun segmentPoint(segment: Segment, pointx: Double, pointy: Double, pointz: Double): Double {
		val ax = pointx - segment.p1.x // a
		val ay = pointy - segment.p1.y
		val az = pointz - segment.p1.z
		val bx = segment.p2.x - segment.p1.x // b
		val by = segment.p2.y - segment.p1.y
		val bz = segment.p2.z - segment.p1.z
		val alen = point(ax, ay, az)
		val blen = point(bx, by, bz)
		val cosalen = MathUtils.dot(ax, ay, az, bx, by, bz) / blen
		if (cosalen == 0.0) {
			return 0.0
		}
		if (cosalen < 0) {
			return alen
		}
		if (cosalen >= blen) {
			return pointPoint(ax, ay, az, bx, by, bz)
		}
		val cos = cosalen / alen
		val sin = Math.sqrt(1.0 - (cos * cos))
		return alen * sin
	}

	@JvmStatic public fun main(args: Array<String>?) {
		val s = Segment(Vector3d(), Vector3d(10.0, 0.0, 0.0))
		val d = segmentPoint(s, 5.0, -10.0, 0.0)
		System.out.println(d)
	}
}