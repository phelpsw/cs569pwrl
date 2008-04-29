package cs569.particles;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class SwarmParticleSystem extends ParticleSystem {

	protected int[] swarmTargets;
	
	public SwarmParticleSystem(int nParticles) {
		super(nParticles);
		
		swarmTargets = new int[nParticles];
	}
	
	/* Emitted particle should also have a follow index */
	@Override
	public int emit(Vector3f position, Vector3f velocity, Color4f color, float mass, float time, Object... params) {
		if (params.length < 1)
			throw new Error("SwarmParticleSystem.emit(): invalid number of objects");
		
		int idx = super.emit(position, velocity, color, mass, time);
		if (idx > 0)
			swarmTargets[idx] = (Integer)params[0];
		
		return idx;
	}
	
	@Override
	protected void evaluateForce(Force force, int idx, Vector3f outForce, Vector3f position, Vector3f velocity, float mass) {
		force.evaluate(outForce, position, velocity, mass, this, swarmTargets[idx]);
	}
	
	@Override
	protected boolean updateParticle(ParticleUpdater updater, int idx, float birthTime, float time, float dt, float mass, Vector3f position, Vector3f velocity, Color4f color) {
		Object[] params = new Object[] { this, swarmTargets[idx] };
		boolean result = updater.updateParticle(birthTime, time, dt, mass, position, velocity, color, params);
		swarmTargets[idx] = (Integer)params[1];
		return result;
		
	}
	
	public void getParticlePosition(int idx, Vector3f outPosition) {
		arrayToVector(particleState, 6*idx, outPosition);
	}
}
