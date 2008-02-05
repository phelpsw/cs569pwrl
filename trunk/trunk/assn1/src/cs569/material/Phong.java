package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.WritingUtils;
import cs569.panel.PhongPanel;
import cs569.shaders.GLSLShader;
import cs569.shaders.PhongShader;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class Phong extends ShaderMaterial {
	// material properties
	protected final Color3f diffuseColor = new Color3f();
	protected final Color3f specularColor = new Color3f();
	protected float P;

	public Phong(Class<? extends GLSLShader> shaderClass) {
		super(shaderClass);
		diffuseColor.set(0.43f, 0.69f, 0.21f);
		specularColor.set(1.0f, 1.0f, 1.0f);
		P = 128.0f;
	}

	public Phong() {
		super(PhongShader.class);
		diffuseColor.set(0.43f, 0.69f, 0.21f);
		specularColor.set(1.0f, 1.0f, 1.0f);
		P = 128.0f;
	}

	public Phong(Color3f diffuse, Color3f specular, float in_P) {
		super(PhongShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new Phong(diffuseColor, specularColor, P);
	}

	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, P);
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

	public float getExponent() {
		return P;
	}

	public void setExponent(double P) {
		this.P = (float) P;
	}

	/**
	 * @see cs569.material.Material#createPropertyPanel()
	 */
	@Override
	public void createPropertyPanel() {
		propertyPanel = new PhongPanel(this);
	}

	/**
	 * @see cs569.material.Material#writeLocalData(java.io.PrintStream, int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeColor(out, diffuseColor, "diffuseColor", indent);
		WritingUtils.writeColor(out, specularColor, "specularColor", indent);
		WritingUtils.writeDouble(out, P, "exponent", indent);
	}
}