/*
 * Created on Feb 1, 2007
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.object;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public interface ParameterizedObjectMaker {
	/**
	 * Construct a parameterized object and return a pointer to its root object.
	 * This method should always be such that a reasonable default object will
	 * be created if zero inputs are supplied.
	 * 
	 * @param inputs
	 * @return
	 */
	public HierarchicalObject make(Object... inputs);
}
