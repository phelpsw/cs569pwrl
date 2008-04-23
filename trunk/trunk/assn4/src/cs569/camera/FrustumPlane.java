package cs569.camera;

import javax.vecmath.Vector3f;

public class FrustumPlane {

	private Vector3f center = new Vector3f();
	private Vector3f normal = new Vector3f();
	
	private float d;

	public FrustumPlane()
	{
		
	}
	
	//set normal and center of plane
	public void setNormalAndPoint(Vector3f normal, Vector3f point)
	{
		this.center.set(point);
		this.normal.set(normal);
		normal.normalize();
		
		d = -(normal.dot(point));
	}
	
	public Vector3f getCenter()
	{
		return center;
	}
	
	public Vector3f getNormal()
	{
		return normal;
	}
	
	public float distance(Vector3f pt)
	{
				
		return (d + normal.dot(pt));
		/*
		float d = center.length();
		float k = (d + (normal.dot(pt)) / (normal.dot(normal)));
		Vector3f closestPoint = new Vector3f(normal);
		closestPoint.scale(k);
		Vector3f diff = new Vector3f(pt);
		diff.sub(closestPoint);
				
		return diff.length();
		*/
	}
}

