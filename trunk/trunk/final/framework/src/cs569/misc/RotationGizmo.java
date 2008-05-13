package cs569.misc;

import java.awt.event.MouseEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Color4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.sun.opengl.util.BufferUtil;

import cs569.apps.TronRuntime;
import cs569.camera.Camera;
import cs569.object.HierarchicalObject;

/**
 * This class renders gizmos for interactive rotations by
 * showing two aligned circles around the bounding sphere
 * of an object. Additionally, a 'handle' position is highlighted
 * at the location where the user has grabbed the gizmo.
 * 
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class RotationGizmo {
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Drawing-related attributes
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	/** Color at the handle */
	protected static final Color4f COLOR1 = new Color4f(1.0f, 1.0f, 1.0f, 0.6f);
	/** Color for the rest of the gizmo */
	protected static final Color4f COLOR2 = new Color4f(1.0f, 1.0f, 1.0f, 0.1f);
	/** Drag arc color */
	protected static final Color4f COLOR3 = new Color4f(0.8f, 0.8f, 1.0f, 0.6f);
	/** Number of vertices per arc */
	protected static final int ARC_RESOLUTION = 128;
	/** Number of arcs that make up this gizmo */
	protected static final int ARC_COUNT = 9;
	/** The vertex array of the gizmo's mesh */
	protected FloatBuffer vertices, verticesCircle;
	/** Per-vertex colors */
	protected FloatBuffer colors;
	/** Indices into the vertex array */
	protected IntBuffer indices, indicesCircle;
	/** Should the gizmo be drawn at all? */
	protected boolean handleVisible, circleVisible;
	/** Radius+distance of the tangent circle */
	protected float tcRadius, tcDist;
	protected Vector3f cameraToSphere;
	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Rotation algorithm-related attributes
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	/** Reference to the viewer object */
	protected TronRuntime viewer;
	/** Reference to the viewport's camera */
	protected Camera camera;
	/** Reference to the currently associated object */
	protected HierarchicalObject object;
	/** World-space version of that object's bounding sphere */
	protected BoundingSphere boundingSphere;
	/** Drag start+end direction in world-space coordinates */
	protected Vector3f dragStart, dragEnd;
	/** Object transform of the associated object before having rotated it */
	protected Quat4f objectRotation;
	/** Inverse of the concatenated rotation transforms along the path to 'object'*/
	protected Matrix3f inverseWorldRotation;

	/**
	 * Create a new invisible rotation gizmo
	 */
	public RotationGizmo(TronRuntime viewer) {
		/* Create enough space for the created geometry */
		vertices = BufferUtil.newFloatBuffer((ARC_RESOLUTION+1)*ARC_COUNT*3);
		indices = BufferUtil.newIntBuffer(ARC_RESOLUTION*ARC_COUNT*2);
		verticesCircle = BufferUtil.newFloatBuffer(3*(ARC_RESOLUTION+1));
		indicesCircle = BufferUtil.newIntBuffer(2*ARC_RESOLUTION+1);
		colors = BufferUtil.newFloatBuffer((ARC_RESOLUTION+1)*ARC_COUNT*4);
		inverseWorldRotation = new Matrix3f();
		objectRotation = new Quat4f();
		handleVisible = false;
		circleVisible = false;
		cameraToSphere = new Vector3f();
		this.viewer = viewer;
		generateCircle();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Algorithm implementation
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Given a position in clip space, this method creates a line between
	 * the near&far planes and intersects it with the world-space bounding
	 * sphere in <code>boundingSphere</code>. If no intersection can be
	 * found, the closest point on that sphere is chosen instead.
	 * @return A spherical direction in world space
	 */
	protected Vector3f project(Point2f posInClipSpace) {
		Matrix4f inverseProjection = camera.getInverseProjectionMatrix();
		Matrix4f inverseView = camera.getInverseViewMatrix();

		/* Convert into a world-space ray using the inverse camera
		   transformations and homogeneous coordinate */
		Vector4f near = new Vector4f(posInClipSpace.x, posInClipSpace.y, 0.0f, 1.0f);
		Vector4f far = new Vector4f(posInClipSpace.x, posInClipSpace.y, 1.0f, 1.0f);
		inverseProjection.transform(near);
		inverseProjection.transform(far);
		near.scale(1.0f/near.w);
		far.scale(1.0f/far.w);
		inverseView.transform(near);
		inverseView.transform(far);

		Point3f nearPt = new Point3f(near.x, near.y, near.z);
		Point3f farPt = new Point3f(far.x, far.y, far.z);
		Vector3f result = new Vector3f();
		Vector3f direction = new Vector3f();
		direction.sub(farPt, nearPt);
		direction.normalize();
		
		/* Try to intersect with the bounding sphere */
		Vector2f intersectionResult = boundingSphere.rayIntersect(nearPt, direction);
		if (intersectionResult != null) {
			/* An intersection was found */
			result.scaleAdd(intersectionResult.x, direction, nearPt);
			result.sub(result, boundingSphere.getCenter());
			result.scale(1.0f / boundingSphere.getRadius());
		} else {
			/* No intersection was found - calculate the closest point on the sphere*/
			float t = boundingSphere.findClosest(nearPt, direction);
			result.scaleAdd(t, direction, nearPt);
			result.sub(result, boundingSphere.getCenter());
			result.normalize();
		}

		return result;
	}

	/**
	 * Given two world-space directions on the bounding sphere,
	 * calculate a <em>local</em> rotation which will move
	 * <code>dragStart</code> onto <code>dragNow</code>.
	 * @return A rotation in the form of an AxisAngle4 object
	 */
	private AxisAngle4f calculateLocalRotation() {
		Vector3f axis = new Vector3f();

		/* Transform the rotations into local space */
		Vector3f dragEnd = new Vector3f(this.dragEnd);
		Vector3f dragStart = new Vector3f(this.dragStart);
		inverseWorldRotation.transform(dragEnd);
		inverseWorldRotation.transform(dragStart);
		dragEnd.normalize(); dragStart.normalize();

		/* Calculate the rotation axis+angle */
		axis.cross(dragStart, dragEnd);

		float angle = (float) Math.acos(dragStart.dot(dragEnd));
		if (axis.length() != 0.0f)
			axis.normalize();

		return new AxisAngle4f(axis, angle);
	}
	
	public void startDrag(MouseEvent e) {
		if (object == null)
			return;
		
		
		/*
		Point2f posInClipSpace = new Point2f(e.getX(), e.getY());
		viewer.windowToViewport(posInClipSpace);
		posInClipSpace.x = Math.max(Math.min(posInClipSpace.x, 1.0f), -1.0f);
		posInClipSpace.y = Math.max(Math.min(posInClipSpace.y, 1.0f), -1.0f);

		boundingSphere = object.getBoundingSphere().transform(object.getWorldTransform());
		
		boundingSphere.setRadius(boundingSphere.getRadius() * 1.005f);

		objectRotation.set(object.getRotation());
		dragStart = project(posInClipSpace);
		dragEnd = null;
		
		Quat4f worldRotation = new Quat4f(0.0f, 0.0f, 0.0f, 1.0f);
		HierarchicalObject node = this.object;
		while (true) {
			worldRotation.mul(node.getRotation(), worldRotation);
			node = (HierarchicalObject) node.getParent();
			if (node == null)
				break;
		}
		worldRotation.inverse();
		inverseWorldRotation.set(worldRotation);
		circleVisible = true;
		update();
		*/
	}

	public void drag(MouseEvent e) {
		if (object == null)
			return;
		Point2f posInClipSpace = new Point2f(e.getX(), e.getY());
		viewer.windowToViewport(posInClipSpace);
		posInClipSpace.x = Math.max(Math.min(posInClipSpace.x, 1.0f), -1.0f);
		posInClipSpace.y = Math.max(Math.min(posInClipSpace.y, 1.0f), -1.0f);

		dragEnd = project(posInClipSpace);

		Quat4f orientation = new Quat4f();
		orientation.set(calculateLocalRotation());
		orientation.mul(objectRotation, orientation);
		object.setRotation(orientation);
		handleVisible = true;
		update();
	}

	public void endDrag(MouseEvent e) {
		handleVisible = false;
		circleVisible = false;
	}

	
	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Access methods
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the currently selected hierarchical object. Invoked
	 * by cs569.apps.Viewer.valueChanged()
	 */
	public void setObject(HierarchicalObject object) {
		this.object = object;
		handleVisible = false;
		circleVisible = false;
	}

	/**
	 * Sets the currently active camera. Invoked by
	 * by cs569.apps.Viewer.renderCamera()
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Display methods
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Regenerate the gizmo's geometry
	 */
	public synchronized void update() {
		vertices.clear();
		indices.clear();
		colors.clear();

		/* Calculate the size of the tangent circle of the bounding sphere. Additionally
		 * the circle is advanced towards the viewer to guarantee its correct position */
		cameraToSphere.sub(boundingSphere.getCenter(), camera.getEye());
		float r = cameraToSphere.length(), R = boundingSphere.getRadius();
		cameraToSphere.scale(1.0f/r);
		tcRadius = R * (float) Math.sqrt(r*r - R*R)/r;
		tcDist = (float) Math.sqrt(R*R - tcRadius*tcRadius);

		if (dragEnd == null)
			return;

		/* Create a local coordinate system */
		Vector3f tangent = new Vector3f();
		tangent.sub(dragStart, dragEnd);
		if (tangent.length() == 0.0f)
			return;
		tangent.normalize();

		Vector3f binormal = new Vector3f();
		binormal.cross(dragStart, tangent);
		binormal.normalize();

		Vector3f p1 = new Vector3f(dragStart);
		p1.scaleAdd(0.3f, tangent, p1); p1.normalize();
		Vector3f p2 = new Vector3f(dragEnd);
		p2.scaleAdd(-0.3f, tangent, p2); p2.normalize();
		Vector3f p3 = new Vector3f(dragEnd);
		p3.scaleAdd(0.3f, binormal, p3); p3.normalize();
		Vector3f p4 = new Vector3f(dragEnd);
		p4.scaleAdd(-0.3f, binormal, p4); p4.normalize();
		Vector3f p5 = new Vector3f(dragEnd);
		p5.scale(-1);

		/* Create a fade-in effect around the handle */
		generateArc(p2, dragEnd, COLOR2, COLOR1);
		generateArc(p3, dragEnd, COLOR2, COLOR1);
		generateArc(p4, dragEnd, COLOR2, COLOR1);
		generateArc(dragStart, p1, COLOR2, COLOR2);
		generateArc(p1, p5, COLOR2, COLOR2);
		generateArc(p2, p5, COLOR2, COLOR2);
		generateArc(p3, p5, COLOR2, COLOR2);
		generateArc(p4, p5, COLOR2, COLOR2);
		generateArc(dragStart, dragEnd, COLOR1, COLOR1);
	}

	/**
	 * Sends the drawing commands to visualize the gizmo
	 */
	public synchronized void glRender(GL gl, GLU glu, Vector3f eye) {
		gl.glUseProgram(0);
		for (int i=0; i<4; i++) {
			gl.glActiveTexture(GL.GL_TEXTURE0+i);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_TEXTURE_CUBE_MAP);
		}
		gl.glLineWidth(2);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

		if (circleVisible) {
			/* Draw the outer circle */
			indicesCircle.rewind(); verticesCircle.rewind();

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glPushMatrix();
			Point3f center = boundingSphere.getCenter();
			gl.glTranslatef(center.x, center.y, center.z);
			gl.glMultMatrixf(GLUtils.fromMatrix4f(camera.getInverseOrientationMatrix()), 0);
			gl.glTranslatef(0, 0, tcDist);
			gl.glScalef(tcRadius, tcRadius, tcRadius);
			gl.glDepthFunc(GL.GL_ALWAYS);
			gl.glDepthMask(false);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 0.1f);
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, verticesCircle);
			gl.glDrawElements(GL.GL_LINES, indicesCircle.capacity(),
					GL.GL_UNSIGNED_INT, indicesCircle);
			gl.glDepthFunc(GL.GL_LEQUAL);
			gl.glDepthMask(true);
			gl.glPopMatrix();
		}

		if (handleVisible) {
			/* Draw the arcs */
			vertices.rewind(); colors.rewind(); indices.rewind();
	
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, vertices);
			gl.glColorPointer(4, GL.GL_FLOAT, 0, colors);
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);
	
			gl.glDrawElements(GL.GL_LINES, indices.capacity(),
					GL.GL_UNSIGNED_INT, indices);
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		}

		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisable(GL.GL_BLEND);
		gl.glLineWidth(1);
	}

	/**
	 * Utility function - creates geometry which represents a spherical
	 * arc between two directions on the bounding sphere. Additionally,
	 * colors are interpolated across this arc.
	 */
	private void generateArc(Vector3f dir1, Vector3f dir2, Color4f color1, Color4f color2) {
		float step = 1.0f / ARC_RESOLUTION;
		Vector3f position = new Vector3f();
		Color4f color = new Color4f();

		for (float v=0.0f; v<=1.0f; v+=step) {
			indices.put(vertices.position()/3);
			if (v+step < 1.0f)
				indices.put(vertices.position()/3+1);
			position.interpolate(dir2, dir1, v);
			position.normalize();
			position.scale(boundingSphere.getRadius());
			position.add(boundingSphere.getCenter());
			vertices.put(position.x);	
			vertices.put(position.y);
			vertices.put(position.z);

			color.interpolate(color2, color1, v);
			colors.put(color.x); colors.put(color.y);
			colors.put(color.z); colors.put(color.w);
		}
	}

	/**
	 * Utility function - creates a simple 2D circle
	 */
	private void generateCircle() {
		float step = 1.0f / ARC_RESOLUTION;
		for (float v=0.0f; v<=1.0f; v+=step) {
			indicesCircle.put(verticesCircle.position()/3);
			if (v+step < 1.0f)
				indicesCircle.put(verticesCircle.position()/3+1);

			float phi = 2.0f* (float) Math.PI*v;
			verticesCircle.put((float) Math.cos(phi));
			verticesCircle.put((float) Math.sin(phi));
			verticesCircle.put(0.0f);
		}
		indicesCircle.put(0);
	}
}
