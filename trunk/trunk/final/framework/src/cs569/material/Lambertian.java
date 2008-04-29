package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.panel.LambertianPanel;
import cs569.shaders.LambertianShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class Lambertian extends ShaderMaterial {

	// material properties
	protected final Color3f diffuseColor = new Color3f();

	public Lambertian() {
		super(LambertianShader.class);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
	}

	public Lambertian(Color3f color) {
		super(LambertianShader.class);
		diffuseColor.set(color);
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new Lambertian(diffuseColor);
	}

	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor);
	}

	@Override
	public void createPropertyPanel() {
		propertyPanel = new LambertianPanel(this);
	}

	/**
	 * @see CS468.Materials.Material#getApproximateDiffuseColor()
	 */
	@Override
	public void getApproximateDiffuseColor(Color3f outColor) {
		outColor.set(diffuseColor);
	}

	// *******************************
	// * Material Property Functions *
	// *******************************
	public Color3f getDiffuseColor() {
		return diffuseColor;
	}

	public void setDiffuseColor(Color3f color) {
		diffuseColor.set(color);
	}

	/**
	 * @see cs569.material.Material#writeLocalData(java.io.PrintStream, int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, diffuseColor, "diffuseColor", indent);
	}
}