package cs569.animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

import cs569.object.HierarchicalObject;

public abstract class Track {

	//TreeSet keyFrame = new TreeSet();
	protected ArrayList <float[]>keyFrame = new ArrayList<float[]>(); //TODO use different structure
	String name;
	protected float maxTime = 0;
	
	public abstract void setObjectTransform(HierarchicalObject object, float time);
	
	public void setObjectName(String name)
	{
		this.name = name;
	}
	
	public String getObjectName()
	{
		return name;
	}
	
	// based on stored max keyframe time, loop the time to within the max
	public float loopTime(float time)
	{
		int count = (int) (time / maxTime); // number of whole loops
		time = time - count * maxTime;
		return time;
	}
}
