package cs569.material;

import javax.vecmath.Color3f;

import cs569.shaders.CityShader;
import cs569.shaders.GLSLShader;
import cs569.texture.Texture;

public class City extends TexturedPhong {
	public City(Class<? extends GLSLShader> shaderClass) {
		super(shaderClass);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
		diffuseTexture = Texture.getTexture("/textures/stoneBrickDiffuse.jpg");
	}

	public City() {
		super(CityShader.class);
		diffuseColor.set(1.0f, 1.0f, 1.0f);
		diffuseTexture = Texture.getTexture("/textures/stoneBrickDiffuse.jpg");
	}

	public City(Color3f diffuse, Color3f specular, float in_P, Texture texture) {
		super(CityShader.class);
		diffuseColor.set(diffuse);
		specularColor.set(specular);
		P = in_P;
		diffuseTexture = texture;
	}

	public Material copy() {
		return new City(diffuseColor, specularColor, P, diffuseTexture);
	}
	
}
