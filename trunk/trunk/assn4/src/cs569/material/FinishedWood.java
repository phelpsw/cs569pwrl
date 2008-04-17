package cs569.material;

import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.panel.FinishedWoodPanel;
import cs569.shaders.FinishedWoodShader;
import cs569.texture.Texture;

public class FinishedWood extends ShaderMaterial {
	protected Texture diffuseTexture, axisTexture, betaTexture, fiberTexture;
	protected Color3f specularColor;
	protected float eta, roughness;

	public FinishedWood() {
		super(FinishedWoodShader.class);
		specularColor = new Color3f(1.0f, 1.0f, 1.0f);
		eta = 1.2f;
		roughness = 0.1f;
		axisTexture = Texture.getTexture("/textures/wood/cmaple-axis.png");
		betaTexture = Texture.getTexture("/textures/wood/cmaple-beta.png");
		fiberTexture = Texture.getTexture("/textures/wood/cmaple-fiber.png");
		diffuseTexture = Texture.getTexture("/textures/wood/cmaple-diffuse.png");
	}

	public FinishedWood(Color3f specular, float eta, float roughness, Texture diffuse,
			Texture axis, Texture beta, Texture fiber) {
		super(FinishedWoodShader.class);
		specularColor = new Color3f(specular);
		this.eta = eta;
		this.roughness = roughness;
		diffuseTexture = diffuse;
		axisTexture = axis;
		betaTexture = beta;
		fiberTexture = fiber;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}

	public void setAxisTexture(Texture axisTexture) {
		this.axisTexture = axisTexture;
	}
	
	public Texture getAxisTexture() {
		return axisTexture;
	}
		
	public void setBetaTexture(Texture betaTexture) {
		this.betaTexture = betaTexture;
	}

	public Texture getBetaTexture() {
		return betaTexture;
	}
	
	public Texture getFiberTexture() {
		return fiberTexture;
	}
	
	public void setFiberTexture(Texture fiberTexture) {
		this.fiberTexture = fiberTexture;
	}
	
	
	public float getEta() {
		return eta;
	}

	public void setEta(float eta) {
		this.eta = eta;
	}
	
	public float getRoughness() {
		return roughness;
	}

	public void setRoughness(float roughness) {
		this.roughness = roughness;
	}

	public Color3f getSpecularColor() {
		return specularColor;
	}
	
	public void setSpecularColor(Color3f specularColor) {
		this.specularColor = specularColor;
	}
	
	/**
	 * @see cs569.material.Material#copy()
	 */
	@Override
	public Material copy() {
		return new FinishedWood(specularColor, eta, roughness, diffuseTexture,
				axisTexture, betaTexture, fiberTexture);
	}
	
	/**
	 * @see cs569.material.ShaderMaterial#configureShader(javax.media.opengl.GL,
	 *      javax.vecmath.Vector3f)
	 */
	@Override
	protected void configureShader(GL gl, Vector3f eye) {
		shader.setGLSLParams(gl, eye, specularColor, eta, roughness,
				diffuseTexture, axisTexture, betaTexture, fiberTexture);
	}	
	
	@Override
	public void createPropertyPanel() {
		propertyPanel = new FinishedWoodPanel(this);
	}
	
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
	}
	
	@Override
	public boolean needsTangentSpace() {
		return true;
	}
}
