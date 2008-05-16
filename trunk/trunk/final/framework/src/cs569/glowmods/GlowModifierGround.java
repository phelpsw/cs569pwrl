package cs569.glowmods;

import java.util.Random;

import javax.vecmath.Color3f;

import cs569.material.Glow;
import cs569.material.Material;

public class GlowModifierGround extends GlowModifier {

	float lastTimeUpdated = -1.0f;
	Random rand;
	float segmentLength = 5000.0f;
	float cycleLength;
	float cycleProgress;
	float segmentProgress;
	
	Color3f color = new Color3f();
	
	public GlowModifierGround(Material mymaterial)
	{
		super("Ground");
		this.mymaterial = mymaterial;
		rand = new Random();
		segmentLength *= rand.nextFloat();
		cycleLength = 6*segmentLength;
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
		float segindicator = cycleProgress / segmentLength;
		if(segindicator > 5) // BR -> Red
		{
			color.set(1,0,1);
			((Glow)mymaterial).setGlowColor(color);
		} else if (segindicator > 4) { // B -> BR
			color.set(0,0,1);
			((Glow)mymaterial).setGlowColor(color);
		} else if (segindicator > 3) { // GB -> B
			color.set(0,1,1);
			((Glow)mymaterial).setGlowColor(color);
		} else if (segindicator > 2) { // G -> GB
			color.set(0,1,0);
			((Glow)mymaterial).setGlowColor(color);
		} else if (segindicator > 1) { // RG -> G
			color.set(1,1,0);
			((Glow)mymaterial).setGlowColor(color);
		} else  { // R -> RG
			color.set(1,0,0);
			((Glow)mymaterial).setGlowColor(color);
		}
		
		lastTimeUpdated = time;
	}

}
