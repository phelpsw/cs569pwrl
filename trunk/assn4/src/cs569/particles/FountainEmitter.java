package cs569.particles;

import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

public class FountainEmitter implements ParticleEmitter {

	Random random;
	float emitRate;
	float emitAccum;
	float spread;
	
	/**
	 * FountainEmitter constructor
	 * @param emitRate The rate at which to emit particles, in particles per second
	 * @param spread The radius of the cone of emission
	 */
	public FountainEmitter(float emitRate, float spread) {
		this.emitRate = 1/emitRate;
		this.emitAccum = 0;
		this.random = new Random();
		this.spread = spread;
	}
	
	public void emitParticles(ParticleSystem particleSystem, float time,
			float dt) {
		
		final Color4f color = new Color4f();
		final Vector3f pos = new Vector3f();
		final Vector3f vel = new Vector3f();
		
		emitAccum += dt;
		
		while (emitAccum > emitRate) {
			
			emitAccum -= emitRate;
			
			pos.set(0, 0, 0);
			
			vel.set(2*random.nextFloat()-1, 0, 2*random.nextFloat()-1);
			vel.normalize();
			vel.scale(random.nextFloat()*spread);
			vel.set(vel.x, 1.5f, vel.z);
			
			particleSystem.emit(pos, vel, color, 1, time);
		}
	}

}
