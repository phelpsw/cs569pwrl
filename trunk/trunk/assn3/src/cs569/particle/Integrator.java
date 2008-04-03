package cs569.particle;

import java.util.ArrayList;

public abstract class Integrator {
	public abstract Particle update(Particle p, ArrayList<Force> forces, float time, float dt);
}
