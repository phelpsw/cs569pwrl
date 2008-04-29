package cs569.particles;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class ParticleColorAttenuator implements ParticleUpdater {

	float maxLife;
	Color4f startColor;
	Color4f endColor;
	
	public ParticleColorAttenuator(float maxLife, Color4f startColor, Color4f endColor) {
		this.maxLife = maxLife;
		this.startColor = startColor;
		this.endColor = endColor;
	}
	
	public boolean updateParticle(float birthTime, float time, float dt, float mass,
			Vector3f position, Vector3f velocity, Color4f color,
			Object... params) {
		
		// Kill the particle if it's too old
		if (time - birthTime > maxLife)
			return false;
		
		// Interpolate color
		color.interpolate(startColor, endColor, (time - birthTime) / maxLife);
		return true;
	}

}
