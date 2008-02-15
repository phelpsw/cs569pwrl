package cs569.material;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.panel.NormalMappedPhongPanel;
import cs569.shaders.NormalMappedPhongShader;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class NormalMappedPhong extends TexturedPhong {
	private Texture normalMapTexture;

	public NormalMappedPhong() {
		super(NormalMappedPhongShader.class);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
		normalMapTexture = Texture.getTexture("/textures/stoneBrickNormal.jpg");

	}

	public NormalMappedPhong(Color3f diffuse, Color3f specular, float in_P, 
				Texture diffuseTex, Texture normalmap) {
		super(NormalMappedPhongShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
		diffuseTexture = diffuseTex;
		normalMapTexture = normalmap;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}
	
	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}
	
	public Texture getNormalMapTexture() {
		return normalMapTexture;
	}
	
	public void setNormalMapTexture(Texture normalMapTexture) {
		this.normalMapTexture = normalMapTexture;
	}
	
	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new NormalMappedPhong(diffuseColor, specularColor, P, diffuseTexture, normalMapTexture);
	}
	
	@Override
	public boolean needsTangentSpace() {
		return true;
	}
	
	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, P, diffuseTexture, normalMapTexture);
	}	

	
	@Override
	public void createPropertyPanel() {
		propertyPanel = new NormalMappedPhongPanel(this);
	}
}