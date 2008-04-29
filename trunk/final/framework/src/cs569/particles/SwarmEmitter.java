package cs569.particles;

import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import java.util.Random;

public class SwarmEmitter implements ParticleEmitter {
	public void emitParticles(ParticleSystem particleSystem, float time,
			float dt) {
		
		final Vector3f pos = new Vector3f();
		final Vector3f vel = new Vector3f();
		final Color4f color = new Color4f(0,0,0,1);
		final Random random = new Random();
		
		int nParticles = particleSystem.maxParticles();
		
		// Fill sphere randomly
		int idx;
		do {
			pos.set(2*random.nextFloat()-1, 2*random.nextFloat()-1, 2*random.nextFloat()-1);
			pos.normalize();
			pos.scale(10*random.nextFloat());
			
			color.x = random.nextFloat();
			
			idx = particleSystem.emit(pos, vel, color, 1.0f, time, random.nextInt(nParticles));
		} while(idx > 0);
	}

}
