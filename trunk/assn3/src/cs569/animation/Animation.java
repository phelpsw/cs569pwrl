package cs569.animation;


import java.util.ArrayList;

import cs569.object.HierarchicalObject;

import sun.text.CompactShortArray.Iterator;

public class Animation implements Animated {

	ArrayList<Track> trackList = new ArrayList<Track>();
	HierarchicalObject rootObject = null;
	
	public void update(float time) {

		if (rootObject == null)
			return;
		
		//System.out.println("update, time =" + time);
		Track t;
		HierarchicalObject o;
		for (int i=0; i<trackList.size(); i++)
		{
			t =trackList.get(i); 
			o = rootObject.findByName(t.getObjectName());
			
			if (o != null)			
				t.setObjectTransform(o, time);				
		}
		
	}
	
	public void addTrack(Track t)
	{
	 trackList.add(t);
	}

	public void setObject(HierarchicalObject o) {
		rootObject = o;
		
	}
	
}
