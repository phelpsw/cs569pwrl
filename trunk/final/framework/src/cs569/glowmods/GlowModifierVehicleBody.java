package cs569.glowmods;

import java.util.Random;

import cs569.material.Glow;
import cs569.material.Material;

public class GlowModifierVehicleBody extends GlowModifier {

	float lastTimeUpdated = -1.0f;
	Random rand;
	float segmentLength = 600.0f;
	float cycleLength;
	float cycleProgress;
	float segmentProgress;
	
	public GlowModifierVehicleBody(Material mymaterial)
	{
		super("VehicleBody");
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
