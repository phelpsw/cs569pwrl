package cs569.particles;

import javax.vecmath.Vector3f;

/**
 * Represents a force that acts on particles.
 * 
 * Created on March 9, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public interface Force {

	public void evaluate(Vector3f outForce, Vector3f position, Vector3f velocity, float mass, Object... params);
}
