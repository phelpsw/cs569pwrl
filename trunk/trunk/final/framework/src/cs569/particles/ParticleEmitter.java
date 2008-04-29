package cs569.particles;

/**
 * Emits particles into a particle system.
 * 
 * Created on March 10, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public interface ParticleEmitter {

	/**
	 * Emit particles into particleSystem via calls to ParticleSystem.emit
	 * @param particleSystem The particle system to emit particles to
	 * @param time The amount of time (in seconds) since the program started
	 * @param dt The amount of time (in seconds) since the particle system was
	 * last updated.
	 */
	public void emitParticles(ParticleSystem particleSystem, float time, float dt);
}
