package cs569.camera;

import javax.vecmath.Vector3f;

public class FrustumPlane 
{

	private float offset;
	private Vector3f normal = new Vector3f();

	public FrustumPlane()
	{
		
	}
	
	//set normal and center of plane
	public void setNormalAndPoint(Vector3f normal, Vector3f point)
	{
		this.normal.set(normal);
		this.normal.normalize();
		
		offset = -(this.normal.dot(point));
	}
	
	public float getOffset()
	{
		return offset;
	}
	
	public Vector3f getNormal()
	{
		return normal;
	}
	
	public float distance(Vector3f pt)
	{
		return (offset + normal.dot(pt));
	}
}

