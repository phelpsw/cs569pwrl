package cs569.glowmods;

import java.util.ArrayList;

public class GlowModifierManager {

	ArrayList<GlowModifier> glowmods = new ArrayList<GlowModifier>();
	
	public GlowModifierManager()
	{
		
	}
	
	public void update(float time)
	{
		for(int i=0; i<glowmods.size(); i++)
		{
			glowmods.get(i).update(time);
		}
	}
	
	public void add(GlowModifier glowmod)
	{
		glowmods.add(glowmod);
	}
	
	public void clear()
	{
		glowmods.clear();
	}
	
}
