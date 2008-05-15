package cs569.glowmods;

import cs569.material.Material;

public abstract class GlowModifier {

	public GlowModifier()
	{
		
	}
	
	public GlowModifier(String name)
	{
		this.name = name;
	}
	
	public String name;
	
	public Material mymaterial;
	
	public abstract void update(float time);
}
