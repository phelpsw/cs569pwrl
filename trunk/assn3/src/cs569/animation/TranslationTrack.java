package cs569.animation;

import javax.vecmath.Vector4f;

import cs569.object.HierarchicalObject;

public class TranslationTrack extends Track {

	public void addKeyFrame(float[] f)
	{
	  keyFrame.add(f);	
	  
	  if (f[0] > maxTime)
		  maxTime = f[0];
	}

	@Override
	public void setObjectTransform(HierarchicalObject object, float time) {
		
		time = loopTime(time);
		
		float[] val;
		float[] prev = null;
				
		for (int i=0; i<keyFrame.size(); i++)
		{
			val = keyFrame.get(i);
			if (val[0] == time)
			{
				object.setTranslate(val[1], val[2], val[3]);
				return;
			}
			else if (val[0] > time && prev != null)
			{
				object.setTranslate(prev[1], prev[2], prev[3]);		
				return;
			}
			prev = val;
				
		}
		
	}
	
	public float[] interpolate(float time, float[] key1, float[] key2)
	{
		if (key2[0] < key1[0])
			System.out.println("error: expecting key2 to be bigger");
		
		float alpha = (time - key1[0]) / (key2[0] - key1[0]); // 0 is key1, 1 is key2
		
		if (alpha < 0 || alpha > 1)
		 System.out.println("alpha is " + alpha + ", key2[0]=" + key2[0] + ", key1[0]=" + key1[0]);
		
		float[] result = new float[key1.length];
		for (int i=0; i<key1.length; i++)
		{
		  result[i] = key1[i] + alpha * (key2[i] - key1[i]);
		}
				
		return result;
		
	}
}
