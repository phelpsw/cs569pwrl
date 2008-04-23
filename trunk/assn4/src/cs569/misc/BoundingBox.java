package cs569.misc;

import javax.vecmath.Vector3f;

public class BoundingBox {

	Vector3f minPoint = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	Vector3f maxPoint = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
	private boolean initialized = false;
	
	public boolean isInitialized()
	{
		return initialized;
	}
	
	public void expandBy(Vector3f point)
	{
		initialized = true;
		
		if (point.x > maxPoint.x)
			maxPoint.x = point.x;		
		if (point.y > maxPoint.y)
			maxPoint.y = point.y;		
		if (point.z > maxPoint.z)
			maxPoint.z = point.z;		
				
		if (point.x < minPoint.x)
			minPoint.x = point.x;		
		if (point.y < minPoint.y)
			minPoint.y = point.y;		
		if (point.z < minPoint.z)
			minPoint.z = point.z;		
		
	}
	
	public void expandBy(BoundingBox b)
	{
		expandBy(b.maxPoint);
		expandBy(b.minPoint);		
	}
	
	
	public Vector3f getVertex(int i)
	{
		if (i==0)
			return minPoint;
		else if (i==1)
			return maxPoint;
		else
			return null;
	}
	
	public String toString()
	{
	 return "(" + minPoint + ", " + maxPoint + ")";
	}
}
