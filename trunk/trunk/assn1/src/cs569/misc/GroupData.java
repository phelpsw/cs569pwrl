package cs569.misc;

import java.util.Vector;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class GroupData {
	public String name;
	public String material;
	public Vector<Vector3f> vertexList = new Vector<Vector3f>();
	public Vector<Vector3f> normalList = new Vector<Vector3f>();
	public Vector<int[]> indexList = new Vector<int[]>();
	public Vector<Vector2f> texcoordList = new Vector<Vector2f>();
}