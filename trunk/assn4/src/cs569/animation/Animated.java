package cs569.animation;

/**
 * This interface should be implemented by all animated
 * objects. When registered with the Viewer class, the
 * update() method will be invoked before starting to draw a new
 * frame.
 *
 * Created on March 5, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public interface Animated {
	/**
	 * Update the animation to reflect its state at the given time
	 * @param time A second-resolution timer (starting at zero
	 * when the program is launched)
	 */
	public void update(float time);
}
