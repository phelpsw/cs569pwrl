package cs569.shaders;



/**
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class CityShader extends TexturedPhongShader {
	/**
	 * Default constructor
	 */
	public CityShader() {
		super("city");
	}

	/**
	 * Default constructor
	 */
	protected CityShader(String identifier) {
		super(identifier);
	}
}