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
}
