package cs569.texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class UndefinedTextureException extends RuntimeException {
	public UndefinedTextureException(String identifier) {
		super("Texture " + identifier + " does not exist");
	}
}
