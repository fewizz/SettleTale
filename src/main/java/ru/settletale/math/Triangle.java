package ru.settletale.math;

public class Triangle {
	Point p1;
	Point p2;
	Point p3;
	
	public void forEachPoint(IPointIter iter) {
		iter.iter(p1);
		iter.iter(p2);
		iter.iter(p3);
	}
	
	public static interface IPointIter {
		public void iter(Point p);
	}
}
