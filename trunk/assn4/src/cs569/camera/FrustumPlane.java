package cs569.camera;

import javax.vecmath.Vector3f;

public class FrustumPlane {

	private Vector3f center;
	private Vector3f normal;

	public FrustumPlane()
	{
		
	}
	
	public void setNormalAndPoint(Vector3f normal, Vector3f point)
	{
		this.center = point;
		this.normal = normal;
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
		float d = center.length();
		float k = (d + (normal.dot(pt)) / (normal.dot(normal)));
		Vector3f closestPoint = new Vector3f(normal);
		closestPoint.scale(k);
		
		pt.sub(closestPoint);
		return pt.length();
	}
}

