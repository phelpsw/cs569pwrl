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
import javax.vecmath.Point4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.sun.opengl.util.BufferUtil;

import cs569.apps.Viewer;
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
	protected Viewer viewer;
	/** Reference to the viewport's camera */
	protected Camera camera;
	/** Reference to the currently associated object */
	protected HierarchicalObject object;
	/** World-space version of that object's bounding sphere */
	protected BoundingSphere boundingSphere;
	/** Drag start+end direction in world-space coordinates */
	protected Vector3f dragStart, dragEnd;

	/**
	 * Create a new invisible rotation gizmo
	 */
	public RotationGizmo(Viewer viewer) {
		/* Create enough space for the created geometry */
		vertices = BufferUtil.newFloatBuffer((ARC_RESOLUTION+1)*ARC_COUNT*3);
		indices = BufferUtil.newIntBuffer(ARC_RESOLUTION*ARC_COUNT*2);
		verticesCircle = BufferUtil.newFloatBuffer(3*(ARC_RESOLUTION+1));
		indicesCircle = BufferUtil.newIntBuffer(2*ARC_RESOLUTION+1);
		colors = BufferUtil.newFloatBuffer((ARC_RESOLUTION+1)*ARC_COUNT*4);
		handleVisible = false;
		circleVisible = false;
		cameraToSphere = new Vector3f();
		this.viewer = viewer;
		generateCircle();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Algorithm implementation
	// //////////////////////////////////////////////////////////////////////////////////////////////////

//	 Calculates closest point on surface of bounding sphere based on radius
	// of the bounding sphere and the direction vector between the closest point
	// on the ray and the center of the bounding sphere
	private void getPointOnBSphere(Point3f rayClosest, BoundingSphere bs, Point3f outPoint3f)
	{
		Vector3f sdir = new Vector3f();
		sdir.sub(rayClosest, bs.getCenter());
		
		float t = bs.getRadius() / sdir.length();
		sdir.scaleAdd(t, bs.getCenter());
		outPoint3f.set(sdir.x,sdir.y,sdir.z);
	}
	
	private void mouseToSpherePoint(int mouseX, int mouseY, Point3f outPoint)
	{
		System.out.println("\n--Begin mouseToSpherePoint--");
		
		//Point2f mouseloc = new Point2f(e.getPoint().x,e.getPoint().y);
		Point2f mouseloc = new Point2f(mouseX,mouseY);
		System.out.println("mouse point: " + mouseloc);
		viewer.windowToViewport(mouseloc);
		System.out.println("viewport mouse: " + mouseloc);
		
//		 Given near plane is 0, far is 1?
		Point4f dirPoint = new Point4f(0.0f, 0.0f, 0.5f, 0.0f);		 	
		Point4f origin = new Point4f(mouseloc.x, mouseloc.y, 1.0f, 1.0f);
		
		// transform ray into worldspace
		Matrix4f Mpi = camera.getInverseProjectionMatrix();
		Matrix4f Mvi = camera.getInverseViewMatrix();
		
		Mpi.transform(dirPoint);
		Mpi.transform(origin);
		
		Mvi.transform(dirPoint);
		Mvi.transform(origin);
		// transformation complete
		
		origin.x /= origin.w;
		origin.y /= origin.w;
		origin.z /= origin.w;
		origin.w /= origin.w;
		
		dirPoint.x /= dirPoint.w;
		dirPoint.y /= dirPoint.w;
		dirPoint.z /= dirPoint.w;
		dirPoint.w /= dirPoint.w;
		
		System.out.println("dirPoint: " + dirPoint);
		
		Point3f origin3f = new Point3f(origin.x, origin.y, origin.z);				
		Vector3f dir3f = new Vector3f(dirPoint.x-origin.x, dirPoint.y-origin.y, dirPoint.z-origin.z);
		dir3f.normalize();
		System.out.println("dir3f:" + dir3f);
		System.out.println("origin: " + origin);

			
		boundingSphere = object.getBoundingSphere().transform(object.getWorldTransform());
		System.out.println("bounding sphere center in world coors: " + boundingSphere.getCenter() + ", rad=" + boundingSphere.getRadius());
				
		Vector2f rayHit = boundingSphere.rayIntersect(origin3f, dir3f);
				
		if (rayHit == null)
		{
			System.out.println("RAY DID NOT HIT BOUNDING SPHERE");
			//operate in world coords
			float tclosest = boundingSphere.findClosest(origin3f, dir3f);
			//float tclosest = object.getBoundingSphere().findClosest(origin3f, dir3f);
					
			Point3f closestToCenter = new Point3f(dir3f);
			closestToCenter.scaleAdd(tclosest, origin3f);
			
			getPointOnBSphere(closestToCenter, boundingSphere, outPoint);
			
		} else
		{
			System.out.println("Ray hit bounding sphere at distances " + rayHit.x + " and " + rayHit.y);
			outPoint.set(dir3f);
			outPoint.scale(rayHit.x); // scale by the near distance
			outPoint.add(origin3f);
		}


		System.out.println("startPoint=" + outPoint);
		System.out.println("--End mouseToSpherePoint--");
	}
	
	public void startDrag(MouseEvent e) {
		
		if (object == null)
			return;
		
		Point3f point = new Point3f();
		mouseToSpherePoint(e.getX(), e.getY(), point);
		
		circleVisible = true;
		handleVisible = true;
				
		
		// dummy values
		dragStart = new Vector3f(1, 0, 0);
		dragStart.normalize();
		dragEnd = new Vector3f(1, .5f, 0);
		dragEnd.normalize();

		update();
	}
	
	public void drag(MouseEvent e) {
		/* To be implemented */
		
		
		update();
	}

	public void endDrag(MouseEvent e) {
		/* To be implemented */
		handleVisible = false;
		circleVisible = false;
		
		if (e== null)
			return;
		
		Point3f point = new Point3f();			
		mouseToSpherePoint(e.getX(), e.getY(), point);
		
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
