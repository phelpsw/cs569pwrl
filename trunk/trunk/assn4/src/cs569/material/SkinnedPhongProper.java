package cs569.material;

import javax.vecmath.Color3f;

import cs569.shaders.SkinnedPhongProperShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class SkinnedPhongProper extends Phong {
	public SkinnedPhongProper() {
		super(SkinnedPhongProperShader.class);
	}

	public SkinnedPhongProper(Color3f diffuse, Color3f specular, float in_P) {
		super(SkinnedPhongProperShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new SkinnedPhongProper(diffuseColor, specularColor, P);
	}
	
	@Override
	public boolean needsTangentSpace() {
		return true;
	}
}