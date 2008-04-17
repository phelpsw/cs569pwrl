package cs569.particles;

import javax.vecmath.Vector3f;
import javax.vecmath.Color4f;

/**
 * Updates particle attributes, such as color, position or velocity.
 * Could be used to attenuate colors or perform collision detection and
 * response with primitives.
 * 
 * Created on March 10, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public interface ParticleUpdater {
	
	/**
	 * Updates a single particle by writing to specified parameters
	 * @param birthTime The time (in seconds since the program started)
	 * that the particle was born.
	 * @param time The current time (in seconds since the program started)
	 * @param mass The particle's mass.
	 * @param position The particle's position.  Overwrite to update.
	 * @param velocity The particle's velocity.  Overwrite to update.
	 * @param color The particle's color. Overwrite to update.
	 * @param params To be used with extended particle systems.
	 * @return true if this particle is to remain alive, false if it is to be killed.
	 */
	public boolean updateParticle(float birthTime, float time, float dt, float mass, Vector3f position, Vector3f velocity, Color4f color, Object... params);
	
}