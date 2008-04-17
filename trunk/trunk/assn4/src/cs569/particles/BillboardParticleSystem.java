package cs569.particles;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs569.texture.Texture;

public class BillboardParticleSystem extends ParticleSystem {

	float[] rotationVels;
	float[] rotations;
	Vector2f [] sizes;
	Texture texture;
	boolean additive;
	
	public BillboardParticleSystem(Texture texture, boolean additive, int nParticles) {
		super(nParticles);
		this.texture = texture;
		this.additive = additive;
		rotationVels = new float[nParticles];
		rotations = new float[nParticles];
		sizes = new Vector2f[nParticles];
		for(int i = 0; i < nParticles; i++)
			sizes[i] = new Vector2f();
	}
	
	/* Emitted particle should also have a follow index */
	@Override
	public int emit(Vector3f position, Vector3f velocity, Color4f color, float mass, float time, Object... params) {
		if (params.length < 1)
			throw new Error("SwarmParticleSystem.emit(): invalid number of objects");
		
		int idx = super.emit(position, velocity, color, mass, time);
		if (idx > 0) {
			rotations[idx] = (Float)params[0];
			rotationVels[idx] = (Float)params[1];
			sizes[idx].set((Vector2f)params[2]);
		}
		return idx;
	}
	
	@Override
	protected boolean updateParticle(ParticleUpdater updater, int idx, float birthTime, float time, float dt, float mass, Vector3f position, Vector3f velocity, Color4f color) {
		Object[] params = new Object[] { rotations[idx], rotationVels[idx], sizes[idx] };
		boolean result = updater.updateParticle(birthTime, time, dt, mass, position, velocity, color, params);
		rotations[idx] = (Float)params[0];
		rotationVels[idx] = (Float)params[1];
		return result;
	}
	
	@Override
	protected void draw(GL gl, GLU glu, Vector3f eye) {
		
		final Vector3f pos = new Vector3f();
		gl.glUseProgram(0);
		texture.bindTexture(gl, 0);
		gl.glEnable(GL.GL_BLEND);
		gl.glDepthMask(false);
		if (additive)
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		else
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		final Vector3f localEye = new Vector3f();
		final Matrix4f inverseWorld = new Matrix4f();
		final Matrix4f particleRotate = new Matrix4f();
		final AxisAngle4f axisAngle = new AxisAngle4f();
		
		final Vector3f particleZ = new Vector3f();
		final Vector3f particleX = new Vector3f();
		final Vector3f particleY = new Vector3f();
		final Vector3f vertex = new Vector3f();
		
		localEye.set(eye);
		inverseWorld.invert(this.getWorldTransform());
		inverseWorld.transform(localEye);
		
		/* Billboard a lot of quads */
		gl.glBegin(GL.GL_QUADS);
		for(int i = 0; i < particleState.length/6; i++) {
			if (alive[i]) {
				
				arrayToVector(particleState, 6*i, pos);
				
				particleZ.sub(localEye, pos);
				particleZ.normalize();
				particleY.set(0,1,0);
				particleX.cross(particleY, particleZ);
				particleX.normalize();
				particleY.cross(particleZ, particleX);
				
				particleX.scale(sizes[i].x/2);
				particleY.scale(sizes[i].y/2);
				
				axisAngle.set(particleZ.x, particleZ.y, particleZ.z, rotations[i]);
				particleRotate.set(axisAngle);
				particleRotate.transform(particleX);
				particleRotate.transform(particleY);
				
				gl.glColor4f(colors[i].x, colors[i].y, colors[i].z, colors[i].w);
				
				vertex.set(pos);
				vertex.add(particleX);
				vertex.add(particleY);
				gl.glTexCoord2f(1, 1);
				gl.glVertex3f(vertex.x, vertex.y, vertex.z);
				
				vertex.set(pos);
				vertex.sub(particleX);
				vertex.add(particleY);
				gl.glTexCoord2f(0, 1);
				gl.glVertex3f(vertex.x, vertex.y, vertex.z);
				
				vertex.set(pos);
				vertex.sub(particleX);
				vertex.sub(particleY);
				gl.glTexCoord2f(0, 0);
				gl.glVertex3f(vertex.x, vertex.y, vertex.z);
				
				vertex.set(pos);
				vertex.add(particleX);
				vertex.sub(particleY);
				gl.glTexCoord2f(1, 0);
				gl.glVertex3f(vertex.x, vertex.y, vertex.z);
			}
		}
		gl.glEnd();
		
		gl.glDepthMask(true);
		gl.glDisable(GL.GL_BLEND);
	}
}
