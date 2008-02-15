package cs569.misc;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Describes a spherical region of space
 * 
 * Created on January 27, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class BoundingSphere {
	private float radius;
	private Point3f center;

	public BoundingSphere() {
		reset();
	}

	public BoundingSphere(BoundingSphere s) {
		center = new Point3f(s.center);
		radius = s.radius;
	}

	public BoundingSphere(Point3f center, float radius) {
		this.center = center;
		this.radius = radius;
	}
	
	public boolean isEmpty() {
		return radius <= 0.0f;
	}

	public void reset() {
		center = new Point3f(0, 0, 0);
		radius = 0.0f;
	}
	
	/**
	 * Expand the bounding sphere to enclose another
	 * point (without moving its center!)
	 */
	public void expandBy(Vector3f p) {
		Vector3f tmp = new Vector3f();
		tmp.sub(p, center);
		float radius = tmp.length();
		if (radius > this.radius)
			this.radius = radius;
	}

	/**
	 * Expand the bounding sphere to enclose another
	 * bounding sphere (without moving its center!)
	 */
	public void expandBy(BoundingSphere s) {
		Vector3f tmp = new Vector3f();
		tmp.sub(s.center, center);
		float length = tmp.length();
		if (length > 0.0f) {
			tmp.scale(1.0f / length * s.radius);
			tmp.add(s.center);
			expandBy(tmp);
		} else {
			radius = Math.max(s.radius, radius);
		}
	}

	/**
	 * Transform the bounding sphere by a matrix
	 * transformation. 
	 */
	public BoundingSphere transform(Matrix4f transform) {
		Point3f newCenter = new Point3f(center);
		transform.transform(newCenter);
		return new BoundingSphere(newCenter, radius * transform.getScale());
	}

	/**
	 * Calculates the intersection points of a given a ray (orig, dir)
	 * and this bounding sphere. Returns a two-dimensional vector
	 * (near, far) containing the distances traveled along the ray.
	 * If no intersections could be found, a null-pointer is
	 * returned instead.
	 */
	public Vector2f rayIntersect(Point3f orig, Vector3f dir) {
		Vector3f originToCenter = new Vector3f();
		originToCenter.sub(center, orig);
		float distToRayClosest = originToCenter.dot(dir);

		if (distToRayClosest < 0.0f) // Ray points away from sphere
			return null;

		float sqrOriginToCenterLength = originToCenter.lengthSquared();
		float sqrHalfChordDist = radius * radius - sqrOriginToCenterLength
			+ distToRayClosest * distToRayClosest;

		if (sqrHalfChordDist < 0) // Miss
			return null;

		// Hit
		float hitDistance = (float) Math.sqrt(sqrHalfChordDist);
		float near = distToRayClosest - hitDistance;
		float far = distToRayClosest + hitDistance;
		if (near == 0)
			near = far;

		return new Vector2f(near, far);
	}

	/**
	 * Given a ray (orig, dir), return the distance
	 * along the ray which is closest to the bounding sphere's center
	 * (disregarding radius)
	 */
	public float findClosest(Point3f orig, Vector3f dir) {
		Vector3f a = new Vector3f();
		a.sub(center, orig);
		return dir.dot(a);
	}

	public Point3f getCenter() {
		return center;
	}
	
	public void setCenter(Point3f center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	@Override
	public String toString() {
		return "BoundingSphere[center=" + center.toString() + ", radius=" + radius + "]";
	}
}
