package cs569.material;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.panel.ShadowedPhongPanel;
import cs569.shaders.ShadowedPhongShader;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class ShadowedPhong extends Phong {
	protected Texture texture;

	public ShadowedPhong() {
		super(ShadowedPhongShader.class);
		texture = Texture.getTexture("Shadow map");
	}

	public ShadowedPhong(Color3f diffuse, Color3f specular, float in_P, Texture texture) {
		super(ShadowedPhongShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
		this.texture = texture;
	}

	public void setShadowMap(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getShadowMap() {
		return texture;
	}

	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, P, texture);
	}	

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new ShadowedPhong(diffuseColor, specularColor, P, texture);
	}
	
	@Override
	public void createPropertyPanel() {
		propertyPanel = new ShadowedPhongPanel(this);
	}
}