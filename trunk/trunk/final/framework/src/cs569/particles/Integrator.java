package cs569.particles;

/**
 * Uses numerical methods to solve differential equations.  'Integrator'
 * is somewhat of a misnomer since integration can really only be used
 * on autonomous systems, whereas here there is clearly time dependence.
 * Integrator performs simple Euler integration, but subclasses can be
 * written to perform any Runge-Kutta, linear multi-step, or implicit
 * method for solving differential equations.
 * 
 * Created on March 10, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public class Integrator {
	
	/* Array used to temporarily store the derivative */
	protected float[] deriv;
	
	/* By default, uses simple Euler integration */
	public void integrate(Integrable integrable, float[] state, float time, float timeStep) {
		
		/* Make sure deriv is the correct size */
		ensureCapacity(state.length);
		
		/* Do a simple euler step */
		integrable.evaluateDerivative(state, time, deriv);
		for(int i = 0; i < state.length; i++)
			state[i] += timeStep*deriv[i];
	}
	
	protected void ensureCapacity(int stateLength) {
		if(deriv == null || deriv.length < stateLength)
			deriv = new float[stateLength];
	}
	
}
