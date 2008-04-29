package cs569.particles;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;

import cs569.animation.Animated;
import cs569.object.HierarchicalObject;

/**
 * Class for CPU-based particle systems.  Handles particle attribute updating,
 * particle emission, differential equations solving, and dead particle management.
 * 
 * Created on March 10, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Donald Holden
 */
public class ParticleSystem extends HierarchicalObject implements Animated, Integrable {

	/* Time management */
	protected float lastTimeUpdated = -1.0f;
	
	/* Particle attributes */
	protected Color4f[] colors;
	protected float[] birthTimes;
	protected float[] masses;
	protected boolean[] alive;
	
	/* State array
	 * [position1, velocity1, position2, velocity2, ...] */
	protected float[] particleState;
	
	/* Forces */
	private ArrayList<Force> forces;
	
	/* Updaters */
	private ArrayList<ParticleUpdater> updaters;
	
	/* Emitters */
	private ArrayList<ParticleEmitter> emitters;
	
	/* Integrators */
	private Integrator integrator = new Integrator();

	/* Dead particle management */
	private Stack<Integer> deadParticles;
	
	
	public ParticleSystem(int nParticles) {
		
		/* Initialize attribute arrays */
		colors = new Color4f[nParticles];
		birthTimes = new float[nParticles];
		masses = new float[nParticles];
		alive = new boolean[nParticles];
		particleState = new float[6*nParticles];
		
		for(int i = 0; i < nParticles; i++) {
			colors[i] = new Color4f();
		}
		
		forces = new ArrayList<Force>();
		updaters = new ArrayList<ParticleUpdater>();
		emitters = new ArrayList<ParticleEmitter>();
		
		/* No particles are alive yet, so fill dead particles stack */
		deadParticles = new Stack<Integer>();
		deadParticles.ensureCapacity(nParticles);
		for(int i = 0; i < nParticles; i++)
			deadParticles.push(i);
	}
	

	public final void evaluateDerivative(float[] state, float time, float[] outDeriv) {
		
		final Vector3f position = new Vector3f();
		final Vector3f velocity = new Vector3f();
		final Vector3f forceVal = new Vector3f();
		
		/* Set v' to 0 and accumulate forces in next loop */
		for(int i = 0; i < state.length/6; i++)
			outDeriv[6*i+3] = outDeriv[6*i+4] = outDeriv[6*i+5] = 0;

		/* Accumulate forces */
		for(Force force : forces) {
			for(int i = 0; i < state.length / 6; i++) {
				
				/* Dead particles don't count */
				if(!alive[i])
					continue;
				
				arrayToVector(state, 6*i,   position);
				arrayToVector(state, 6*i+3, velocity);
				
				/* Get the force on a particle given particle parameters */
				evaluateForce(force, i, forceVal, position, velocity, masses[i]);
				
				/* Accumulate force */
				outDeriv[6*i+3] += forceVal.x;
				outDeriv[6*i+4] += forceVal.y;
				outDeriv[6*i+5] += forceVal.z;
			}
		}
		
		/* Set the derivatives */
		for(int i = 0; i < state.length/6; i++) {

			/* x' = v */
			outDeriv[6*i+0] = state[6*i+3];
			outDeriv[6*i+1] = state[6*i+4];
			outDeriv[6*i+2] = state[6*i+5];

			/* Forces already accumulated, and F/m = a = v' */
			outDeriv[6*i+3] /= masses[i];
			outDeriv[6*i+4] /= masses[i];
			outDeriv[6*i+5] /= masses[i];
		}
	}
	
	/* Override for extended particle systems */
	public int emit(Vector3f position, Vector3f velocity, Color4f color, float mass, float time, Object... params) {

		/* No space for new particle! */
		if(deadParticles.isEmpty())
			return -1;

		int idx = deadParticles.pop();
		
		vectorToArray(particleState, 6*idx, position);
		vectorToArray(particleState, 6*idx+3, velocity);
		
		colors[idx].set(color);
		masses[idx] = mass;
		birthTimes[idx] = time;
		alive[idx] = true;
		
		return idx;
	}
	
	public void addForce(Force f) {
		forces.add(f);
	}
	
	public void addUpdater(ParticleUpdater updater) {
		updaters.add(updater);
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		emitters.add(emitter);
	}
	
	public void setIntegrator(Integrator integrator) {
		this.integrator = integrator;
	}
	
	public int maxParticles() {
		return particleState.length/6;
	}
	
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) {
		
		final Vector3f pos = new Vector3f();
		gl.glPointSize(3);
		gl.glUseProgram(0);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		/* Just draw a bunch of GL_POINTS */
		gl.glBegin(GL.GL_POINTS);
		for(int i = 0; i < particleState.length/6; i++) {
			if (alive[i]) {
				arrayToVector(particleState, 6*i, pos);
				gl.glColor4f(colors[i].x, colors[i].y, colors[i].z, colors[i].w);
				gl.glVertex3f(pos.x, pos.y, pos.z);
			}
		}
		gl.glEnd();
		
		gl.glDisable(GL.GL_BLEND);
	}

	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		// TODO Auto-generated method stub

	}

	public final void update(float time) {
		
		final Vector3f position = new Vector3f();
		final Vector3f velocity = new Vector3f();
		
		if (lastTimeUpdated < 0) {
			lastTimeUpdated = time;
			return;
		}
		
		float dt = time - lastTimeUpdated;
		
		/* Update particles */
		for(ParticleUpdater updater : updaters) {
			for(int i = 0; i < particleState.length / 6; i++) {
				/* Dead particles don't need to be updated */
				if(!alive[i])
					continue;
				
				arrayToVector(particleState, 6*i,   position);
				arrayToVector(particleState, 6*i+3, velocity);
				
				if(!updateParticle(updater, i, birthTimes[i], time, dt, masses[i], position, velocity, colors[i])) {
					alive[i] = false;
					deadParticles.push(i);
				}
				
				vectorToArray(particleState, 6*i, position);
				vectorToArray(particleState, 6*i+3, velocity);
			}
		}
		
		/* Move the system forward in time */
		integrator.integrate(this, particleState, lastTimeUpdated, dt);
		
		/* Emit particles */
		for(ParticleEmitter emitter : emitters) {
			emitter.emitParticles(this, time, dt);
		}
		
		lastTimeUpdated = time;
	}
	
	protected boolean updateParticle(ParticleUpdater updater, int idx, float birthTime, float time, float dt, float mass, Vector3f position, Vector3f velocity, Color4f color) {
		return updater.updateParticle(birthTime, time, dt, mass, position, velocity, color);
	}
	
	/* Override this method to call forces with extended args */
	protected void evaluateForce(Force force, int idx, Vector3f outForce, Vector3f position, Vector3f velocity, float mass) {
		force.evaluate(outForce, position, velocity, mass);
	}
	
	protected void arrayToVector(float[] array, int offset, Vector3f vector) {
		vector.set(array[offset+0], array[offset+1], array[offset+2]);
	}
	
	protected void vectorToArray(float[] array, int offset, Vector3f vector) {
		array[offset+0] = vector.x;
		array[offset+1] = vector.y;
		array[offset+2] = vector.z;
	}

}