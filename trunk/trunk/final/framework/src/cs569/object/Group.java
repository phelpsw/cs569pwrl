package cs569.object;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class Group extends HierarchicalObject {

	/**
	 * Should be used only be the Parser. Please name your objects!
	 */
	public Group() {
		super("Group " + getUniqueID());
	}

	public Group(String inName) {
		super(inName);
	}

	/**
	 * @see cs569.object.HierarchicalObject#draw(net.java.games.jogl.GL,
	 *      net.java.games.jogl.GLU, javax.vecmath.Vector3f)
	 */
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) {
		// Nothing done. All geometry is in the children.
	}

	/**
	 * @see cs569.object.HierarchicalObject#writeLocalData(java.io.PrintStream,
	 *      int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// Nothing written. All geometry is in the children.
	}
}
