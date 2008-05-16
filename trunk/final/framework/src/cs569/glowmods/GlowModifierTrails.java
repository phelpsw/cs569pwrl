package cs569.glowmods;

import java.util.Random;

import cs569.apps.TronRuntime;
import cs569.material.Glow;
import cs569.material.Material;

public class GlowModifierTrails extends GlowModifier {

	float lastTimeUpdated = -1.0f;
	Random rand;
	float segmentLength = 3000.0f;
	float cycleLength;
	float cycleProgress;
	float segmentProgress;
	
	public GlowModifierTrails(Material mymaterial)
	{
		super("Trail");
		this.mymaterial = mymaterial;
		rand = new Random();
		segmentLength *= rand.nextFloat();
		cycleLength = 2*segmentLength;
		cycleProgress = 0;
	}
	
	@Override
	public void update(float time) {
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = time;
			return;
		}
		// dt is in ms
		float dt = (time - lastTimeUpdated)*1000.0f;
		
		cycleProgress += dt;
		cycleProgress = cycleProgress % cycleLength;
		
		segmentProgress = (cycleProgress % segmentLength) / segmentLength;
		
		if(cycleProgress > segmentLength)
		{
			((Glow)mymaterial).setGlowFactor(1-(segmentProgress));
		} else {
			((Glow)mymaterial).setGlowFactor(segmentProgress);
		}
		
		lastTimeUpdated = time;
	}

}
