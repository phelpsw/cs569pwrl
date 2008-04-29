package cs569.particles;

import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class SwarmUpdater implements ParticleUpdater {
	public boolean updateParticle(float birthTime, float time, float dt, float mass,
			Vector3f position, Vector3f velocity, Color4f color,
			Object... params) {
		
		final Vector3f followPos = new Vector3f();
		final Random random = new Random();
		
		SwarmParticleSystem sys = (SwarmParticleSystem)params[0];
		int followIdx = (Integer)params[1];
		sys.getParticlePosition(followIdx, followPos);
		
		velocity.sub(followPos, position);
		float dist = velocity.length();
		velocity.normalize();
		velocity.scale(dist-0.01f);
			
		return true;
	}

}
