package cs569.particles;

/**
 * This interface should be implemented by classes with differential equations
 * to solve.
 * 
 * Created on March 9, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public interface Integrable {
	
	/**
	 * Evaluate the derivative of the system at a particular time
	 * @param state The state of the system at the given time
	 * @param time A second-resolution timer (starting at zero
	 * when the program is launched)
	 * @param outDerivative An array of length equal to the length
	 * of state that should be set to reflect the derivative of the
	 * system at the given time
	 */
	public void evaluateDerivative(float[] state, float time, float[] outDerivative);
}
