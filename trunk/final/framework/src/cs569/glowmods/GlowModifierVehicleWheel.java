package cs569.glowmods;

import java.util.Random;

import cs569.material.Glow;
import cs569.material.Material;

public class GlowModifierVehicleWheel extends GlowModifier {

	float lastTimeUpdated = -1.0f;
	Random rand = new Random();
	
	// Length of birth in counts
	static int sequencePeriod = 2;
	static int sequenceComponentPeriod;
	static int sequenceComponents = 2;
	int sequencePosition = 0;
	
	public GlowModifierVehicleWheel(Material mymaterial)
	{
		super("VehicleWheel");
		this.mymaterial = mymaterial;

		sequenceComponentPeriod = sequencePeriod / sequenceComponents;
	}
	
	@Override
	public void update(float time) {
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = time;
			return;
		}
		// dt is in ms
		float dt = (time - lastTimeUpdated)*1000.0f;
		
		sequencePosition++;
		sequencePosition = sequencePosition % sequencePeriod;
		
		if(sequencePosition == 0) 
			((Glow)mymaterial).setGlowFactor(1);
		else if (sequencePosition == sequenceComponentPeriod)
			((Glow)mymaterial).setGlowFactor(0);
		
		
		lastTimeUpdated = time;
	}

}
