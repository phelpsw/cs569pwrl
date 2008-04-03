package cs569.animation;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector4f;

import cs569.object.HierarchicalObject;

public class OrientationTrack extends Track{
	
	public void addKeyFrame(float[] f)
	{
	  keyFrame.add(f);	
	  if (f[0] > maxTime)
		  maxTime = f[0];
	}

	@Override
	public void setObjectTransform(HierarchicalObject object, float time) {
		time = loopTime(time);			
		
		int beginIndex;
		//start looking in the list where the last frame was to minimize searching
		if (lastKeyframeIndex >= 0 && lastKeyframeIndex < keyFrame.size() && time >= keyFrame.get(lastKeyframeIndex)[0])
			beginIndex = lastKeyframeIndex;
		 else
			beginIndex = 0;
		
	
		for (int i=beginIndex; i<keyFrame.size()-1; i++)
		{			
			if (keyFrame.get(i)[0] <= time && keyFrame.get(i+1)[0] >= time)
			{				
				lastKeyframeIndex = i;
				object.setRotation(interpolate(time, keyFrame.get(i), keyFrame.get(i+1)));
				return;
			}								
		}
		//System.out.println("no key, lastIndex=" + lastKeyframeIndex + ", time=" + time);
		
	}

	
	public Quat4f interpolate(float time, float[] key1, float[] key2) {

		float alpha = (time - key1[0]) / (key2[0] - key1[0]); // 0 is key1, 1 is key2
		
		Quat4f q1 = new Quat4f(key1[1], key1[2], key1[3], key1[4]);
		Quat4f q2 = new Quat4f(key2[1], key2[2], key2[3], key2[4]);
		
		q1.interpolate(q1, q2, alpha);
		return q1;					
		
	}

		

}
