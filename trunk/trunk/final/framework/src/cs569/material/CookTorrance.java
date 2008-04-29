/*
 * Created on Jan 25, 2007
 * Copyright 2005 Program of Computer Grpahics, Cornell University
 */
package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.panel.CookTorrancePanel;
import cs569.shaders.CookTorranceShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class CookTorrance extends ShaderMaterial {

	// material properties
	protected final Color3f diffuseColor = new Color3f();
	protected final Color3f specularColor = new Color3f();
	protected float M;
	protected float N;

	public CookTorrance() {
		super(CookTorranceShader.class);
		diffuseColor.set(0.3f, 0.3f, 0.5f);
		specularColor.set(1.0f, 1.0f, 1.0f);
		M = 0.3f;
		N = 1.7f;
	}

	public CookTorrance(Color3f diffuse, Color3f specular, float inM,
			float inN) {
		super(CookTorranceShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		M = inM;
		N = inN;
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new CookTorrance(diffuseColor, specularColor, M, N);
	}

	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, M, N);
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

	public float getM() {
		return M;
	}

	public void setM(double inM) {
		this.M = (float) inM;
	}

	public float getN() {
		return N;
	}

	public void setN(double inN) {
		this.N = (float) inN;
	}

	/**
	 * @see cs569.material.Material#createPropertyPanel()
	 */
	@Override
	public void createPropertyPanel() {
		propertyPanel = new CookTorrancePanel(this);
	}

	/**
	 * @see cs569.material.Material#writeLocalData(java.io.PrintStream, int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, diffuseColor, "diffuseColor", indent);
		WritingUtils.writeColor(out, specularColor, "specularColor", indent);
		WritingUtils.writeDouble(out, M, "M", indent);
		WritingUtils.writeDouble(out, N, "N", indent);
	}
}
