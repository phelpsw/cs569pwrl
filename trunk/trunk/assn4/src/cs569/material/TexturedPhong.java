package cs569.material;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.panel.TexturedPhongPanel;
import cs569.shaders.GLSLShader;
import cs569.shaders.TexturedPhongShader;
import cs569.texture.Texture;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class TexturedPhong extends Phong {
	protected Texture diffuseTexture;

	public TexturedPhong(Class<? extends GLSLShader> shaderClass) {
		super(shaderClass);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
		diffuseTexture = Texture.getTexture("/textures/stoneBrickDiffuse.jpg");
	}

	public TexturedPhong() {
		super(TexturedPhongShader.class);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
		diffuseTexture = Texture.getTexture("/textures/stoneBrickDiffuse.jpg");
	}

	public TexturedPhong(Color3f diffuse, Color3f specular, float in_P, Texture texture) {
		super(TexturedPhongShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
		diffuseTexture = texture;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}
	

	public void setDiffuseTextureName(String diffuseTexture) {
		this.diffuseTexture = Texture.getTexture(diffuseTexture);
	}

	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new TexturedPhong(diffuseColor, specularColor, P, diffuseTexture);
	}
	
	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, diffuseColor, specularColor, P, diffuseTexture);
	}	

	
	@Override
	public void createPropertyPanel() {
		propertyPanel = new TexturedPhongPanel(this);
	}
}