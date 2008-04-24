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
	public void setNormalAndPoint(Vector3f norm, Vector3f point)
	{
		normal.set(norm);
		normal.normalize();
		
		offset = -(normal.dot(point));		
	}
	
	public void setCoefficients(float a, float b, float c, float d) 
	{
		// set the normal vector
		normal.set(a,b,c);
		//compute the length of the vector
		float l = normal.length();
		// normalize the vector
		normal.set(a/l,b/l,c/l);
		// and divide d by th length as well
		offset = d/l;
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

