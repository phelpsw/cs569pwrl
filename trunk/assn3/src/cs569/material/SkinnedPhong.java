package cs569.material;

import javax.vecmath.Color3f;

import cs569.shaders.SkinnedPhongShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class SkinnedPhong extends Phong {
	public SkinnedPhong() {
		super(SkinnedPhongShader.class);
	}

	public SkinnedPhong(Color3f diffuse, Color3f specular, float in_P) {
		super(SkinnedPhongShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
	}
	
	public boolean needsSkin() {
		return true;		
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new SkinnedPhong(diffuseColor, specularColor, P);
	}
}