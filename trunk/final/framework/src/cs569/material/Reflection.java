package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.panel.ReflectionPanel;
import cs569.shaders.ReflectionShader;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class Reflection extends ShaderMaterial {
	protected Texture texture;

	public Reflection() {
		super(ReflectionShader.class);
		texture = Texture.getTexture("Backyard");
	}

	public Reflection(Texture texture) {
		super(ReflectionShader.class);
		this.texture = texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return texture;
	}

	@Override
	public Material copy() {
		return new Reflection(texture);
	}

	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, texture);
	}

	@Override
	public void createPropertyPanel() {
		propertyPanel = new ReflectionPanel(this);
	}

	@Override
	public void getApproximateDiffuseColor(Color3f outColor) {
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
	}
}