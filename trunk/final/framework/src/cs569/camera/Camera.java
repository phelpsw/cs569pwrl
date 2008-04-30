package cs569.camera;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob
 */
public class Camera {
	/**
	 * The upper limit of the theta angle allowed when orbiting. Avoids gimbal
	 * lock
	 */
	static final float THETA_LIMIT = 89.0f * (float) Math.PI / 180.0f;

	/** The YFOV in degrees */
	protected float fov = 65f;

	/** The near plane distance */
	protected float near = 0.1f;

	/** The far plane distance */
	protected float far = 1000f;

	/** The aspect ratio */
	protected float aspect = 1;

	/** Projection matrix */
	private Matrix4f projection = new Matrix4f();
	private Matrix4f inverseProjection = new Matrix4f();

	/** Orientation matrix (view w/o translate) */
	private Matrix4f orientation = new Matrix4f();
	private Matrix4f inverseOrientation = new Matrix4f();

	/** View matrix */
	private Matrix4f view = new Matrix4f();
	private Matrix4f inverseView = new Matrix4f();

	// The position and orientation of the camera
	protected Vector3f eye = new Vector3f();
	protected Vector3f target = new Vector3f();
	protected Vector3f up = new Vector3f();

	// Some temporary space for computing camera motions and transformations
	protected Vector3f u = new Vector3f();
	protected Vector3f v = new Vector3f();
	protected Vector3f w = new Vector3f();
	protected Vector3f q = new Vector3f();
	protected Matrix3f basis = new Matrix3f();
	private Matrix4f translate = new Matrix4f();

	/**
	 * The default camera position
	 */
	public Camera() {
		eye.set(5, 3, -3);
		target.set(0, 0, 0);
		up.set(0, 1, 0);
	}

	/**
	 * Create a camera with a specific position
	 * 
	 * @param eye
	 * @param target
	 * @param up
	 * @param camName
	 */
	public Camera(Vector3f eye, Vector3f target, Vector3f up, float yFov,
			float near, float far) {
		this.eye.set(eye);
		this.target.set(target);
		this.up.set(up);
		fov = yFov;
		this.near = near;
		this.far = far;

	}
	
	/**
	 * Update the projection and view matrices
	 */
	public void updateMatrices() {
		/**
		 * Generate a perspective projection matrix for OpenGL
		 * (See: http://www.opengl.org/documentation/specs/man_pages/hardcopy/GL/html/glu/perspective.html)
		 * Avoid setting near too close to 0 or much Z-buffer precision will be lost!
		 */
		float thetaY = (float) Math.PI / 180.0f * (fov / 2);
		float tanThetaY = (float) Math.tan(thetaY);
		float h = 1 / tanThetaY;
		float ww = h / aspect;
		float qq = -(far + near) / (far - near);
		float qn = -2 * (far * near) / (far - near);

		projection.setZero();
		projection.m00 = ww;
		projection.m11 = h;
		projection.m22 = qq;
		projection.m23 = qn;
		projection.m32 = -1.0f;
		inverseProjection.invert(projection);

		/**
		 * Generate a view transformation matrix for OpenGL
		 * (See: http://www.opengl.org/documentation/specs/man_pages/hardcopy/GL/html/glu/lookat.html)
		 */
		u.sub(target, eye);
		u.normalize();
		v.set(up);
		v.normalize();
		w.cross(u, v);
		q.cross(w, u);
		w.normalize();
		q.normalize();

		orientation.setIdentity();
		translate.setIdentity();
		orientation.m00 = w.x; orientation.m01 = w.y; orientation.m02 = w.z;
		orientation.m10 = q.x; orientation.m11 = q.y; orientation.m12 = q.z;
		orientation.m20 = -u.x; orientation.m21 = -u.y; orientation.m22 = -u.z;
		translate.m03 = -eye.x;
		translate.m13 = -eye.y;
		translate.m23 = -eye.z;
		inverseOrientation.transpose(orientation);
		view.mul(orientation, translate);
		inverseView.invert(view);
	}

	/**
	 * Return the orientation matrix
	 */
	public Matrix4f getOrientationMatrix() {
		return orientation;
	}

	/**
	 * Return the inverse of the orientation matrix
	 */
	public Matrix4f getInverseOrientationMatrix() {
		return inverseOrientation;
	}
	
	/**
	 * Return the view matrix
	 */
	public Matrix4f getViewMatrix() {
		return view;
	}

	/**
	 * Return the inverse of the view matrix
	 */
	public Matrix4f getInverseViewMatrix() {
		return inverseView;
	}

	/**
	 * Return the projection matrix
	 */
	public Matrix4f getProjectionMatrix() {
		return projection;
	}

	/**
	 * Return the inverse of the projection matrix
	 */
	public Matrix4f getInverseProjectionMatrix() {
		return inverseProjection;
	}

	/**
	 * Returns the eye of the camera.
	 * 
	 * @return
	 */
	public Vector3f getEye() {
		return new Vector3f((float) eye.x, (float) eye.y, (float) eye.z);
	}

	/**
	 * Sets the eye of the camera.
	 */
	public void setEye(Vector3f eyeVec) {
		eye.set(eyeVec);
	}

	/**
	 * Sets the target of the camera.
	 */
	public void setTarget(Vector3f targetVec) {
		target.set(targetVec);
	}
	
	/**
	 * Sets the target of the camera.
	 */
	public void setTarget(float x, float y, float z) {
		target.x = x;
		target.y = y;
		target.z = z;
	}

	/**
	 * Sets the direction of the camera (based on the current eye pos.)
	 */
	public void setDirection(Vector3f direction) {
		target.set(eye);
		target.add(direction);
	}

	/**
	 * Sets the up vector
	 */
	public void setUp(Vector3f up) {
		this.up.set(up);
	}	
	
	/**
	 * Returns 0 if a is smallest, 1 if b is smallest, 2 if c is smallest.
	 */
	static private int argmin(double a, double b, double c) {
		return a < b ? (a < c ? 0 : 2) : (b < c ? 1 : 2);
	}

	/**
	 * Returns a vector that is not nearly parallel to v.
	 */
	static private Vector3f nonParallelVector(Vector3f v) {
		int i = argmin(Math.abs(v.x), Math.abs(v.y), Math.abs(v.z));
		Vector3f u = new Vector3f();
		if (i == 0) {
			u.x = 1;
		} else if (i == 1) {
			u.y = 1;
		} else if (i == 2) {
			u.z = 1;
		}
		return u;
	}

	/**
	 * Moves the camera forwards and backwards relative to the target.
	 * 
	 * @param last
	 * @param cur
	 */
	public void dolly(float amount) {
		Vector3f diff = new Vector3f();
		diff.sub(eye, target);
		diff.normalize();
		eye.scaleAdd(amount, diff, eye);
		target.scaleAdd(amount, diff, target);
	}
	
	public void strafe(float amount) {
		Vector3f dir = new Vector3f();
		dir.sub(target, eye);
		Vector3f right = new Vector3f();
		right.cross(dir, up);
		right.normalize();
		eye.scaleAdd(amount, right, eye);
		target.scaleAdd(amount, right, target);
	}
	
	public void yaw(float amount) {
		Matrix3f mtx = new Matrix3f();
		Vector3f dir = new Vector3f();
		dir.sub(target, eye);
		dir.normalize();
		mtx.set(new AxisAngle4f(up, -amount));
		mtx.transform(dir);
		target.add(eye, dir);
	}

	public void pitch(float amount) {
		Matrix3f mtx = new Matrix3f();
		Vector3f dir = new Vector3f();
		dir.sub(target, eye);
		dir.normalize();

		Vector3f right = new Vector3f();
		right.cross(dir, up);
		right.normalize();
		mtx.set(new AxisAngle4f(right, amount));
		mtx.transform(dir);
		target.add(eye, dir);
	}

	/**
	 * Orbits the camera around the target.
	 * 
	 * @param lastMousePoint
	 * @param currMousePoint
	 */
	public void orbit(Point2f lastMousePoint, Point2f currMousePoint) {

		Vector2f mouseDelta = new Vector2f(currMousePoint);
		mouseDelta.sub(lastMousePoint);

		// Construct an arbitrary frame at the target with the z-axis the up
		// vector
		w.set(up);
		w.normalize();
		u.set(nonParallelVector(w));
		v.cross(w, u);
		v.normalize();
		u.cross(v, w);
		basis.setColumn(0, u);
		basis.setColumn(1, v);
		basis.setColumn(2, w);
		Matrix4f frame = new Matrix4f(basis, target, 1);
		Matrix4f frameInv = new Matrix4f();
		frameInv.invert(frame);

		// write eye in that frame
		Vector3f e = new Vector3f(eye);
		frameInv.transform(e);

		// write e in spherical coordinates
		float r = e.length();
		float phi = (float) Math.atan2(e.y, e.x);
		float theta = (float) Math.asin(e.z / r);

		// increment phi and theta by mouse motion
		phi += -Math.PI / 2 * mouseDelta.x;
		theta += -Math.PI / 2 * mouseDelta.y;
		if (theta > THETA_LIMIT) {
			theta = THETA_LIMIT;
		}
		if (theta < -THETA_LIMIT) {
			theta = -THETA_LIMIT;
		}

		// write e back in cartesian world coords
		e.set(
			(float) (r * Math.cos(phi) * Math.cos(theta)),
			(float) (r * Math.sin(phi) * Math.cos(theta)),
			(float) (r * Math.sin(theta))
		);

		eye.normalize();
		frame.transform(e, eye);

	}

	public void setAspect(float d) {
		aspect = d;
	}

	public Vector3f getTarget() {
		return target;
	}
	
	public void setYFOV(float yFOV) {
		this.fov = yFOV;
	}
}