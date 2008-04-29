package cs569.particles;

import javax.vecmath.Vector3f;

public class SwarmForce implements Force {
	public void evaluate(Vector3f outForce, Vector3f position,
			Vector3f velocity, float mass, Object... params) {
		
		final Vector3f followPos = new Vector3f();
		
		SwarmParticleSystem sys = (SwarmParticleSystem)params[0];
		int followIdx = (Integer)params[1];
		
		sys.getParticlePosition(followIdx, followPos);
		
		outForce.sub(followPos, position);
		outForce.scaleAdd(-1.0f, velocity, outForce);
		outForce.scaleAdd(-0.1f, position, outForce);
	}

}
