package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.panel.AnisotropicWardPanel;
import cs569.shaders.AnisotropicWardShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class AnisotropicWard extends ShaderMaterial {

	// material properties
	protected final Color3f diffuseColor = new Color3f();
	protected final Color3f specularColor = new Color3f();
	protected float alphaX, alphaY;

	public AnisotropicWard() {
		super(AnisotropicWardShader.class);
		diffuseColor.set(0.2f, 0.3f, 0.1f);
		specularColor.set(0.6f, 0.8f, 0.6f);
		alphaX = 0.4f;
		alphaY = 0.2f;
	}

	public AnisotropicWard(Color3f diffuse, Color3f specular, float in_alphax, float in_alphay) {
		super(AnisotropicWardShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		alphaX = in_alphax;
		alphaY = in_alphay;
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new AnisotropicWard(diffuseColor, specularColor, alphaX, alphaY);
	}

	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, alphaX, alphaY);
	}

	@Override
	public boolean needsTangentSpace() {
		return true;
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

	public Color3f getSpecularColor() {
		return specularColor;
	}

	public void setSpecularColor(Color3f color) {
		specularColor.set(color);
	}

	public float getAlphaX() {
		return alphaX;
	}

	public void setAlphaX(double alpha) {
		this.alphaX = (float) alpha;
	}

	public float getAlphaY() {
		return alphaY;
	}

	public void setAlphaY(double alpha) {
		this.alphaY = (float) alpha;
	}

	/**
	 * @see cs569.material.Material#createPropertyPanel()
	 */
	@Override
	public void createPropertyPanel() {
		propertyPanel = new AnisotropicWardPanel(this);
	}

	/**
	 * @see cs569.material.Material#writeLocalData(java.io.PrintStream, int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, diffuseColor, "diffuseColor", indent);
		WritingUtils.writeColor(out, specularColor, "specularColor", indent);
		WritingUtils.writeDouble(out, alphaX, "alphaX", indent);
		WritingUtils.writeDouble(out, alphaY, "alphaY", indent);
	}
}

//