package cs569.particles;

import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class ExplosionEmitter implements ParticleEmitter {

	Random random;
	int currentCount;
	int totalCount;
	float emitAccum;
	float spread;
	Vector3f pos;
	
	/**
	 * FountainEmitter constructor
	 * @param emitRate The rate at which to emit particles, in particles per second
	 * @param spread The radius of the cone of emission
	 */
	public ExplosionEmitter(int totalCount, float spread, Vector3f pos) {
		this.totalCount = totalCount;
		this.currentCount = 0;
		this.random = new Random();
		this.spread = spread;
		this.pos = pos;
	}
	
	public void emitParticles(ParticleSystem particleSystem, float time, float dt) 
	{
	
		final Color4f color = new Color4f();
		final Vector3f vel = new Vector3f();
		
		while (currentCount < totalCount) {
			
			vel.set(2*random.nextFloat()-1, 0, 2*random.nextFloat()-1);
			vel.normalize();
			vel.scale(random.nextFloat()*spread);
			vel.set(vel.x, 1.5f, vel.z);
			
			particleSystem.emit(pos, vel, color, 1, time);
			currentCount++;
		}
	}
}

