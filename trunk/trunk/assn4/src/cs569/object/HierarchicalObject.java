package cs569.object;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import cs569.camera.Camera;
import cs569.material.Lambertian;
import cs569.material.Material;
import cs569.misc.BoundingBox;
import cs569.misc.BoundingSphere;
import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.misc.WritingUtils;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public abstract class HierarchicalObject implements MutableTreeNode,
		Iterable<HierarchicalObject> {

	/** The base list of matrices to update */
	protected static final int[] NORMAL_UPDATE = new int[] { GL.GL_MODELVIEW };
	protected static final int[] NO_UPDATE = new int[] { };

	/**
	 * If the global material is set, the local materials of all objects are
	 * ignored and the global material is used instead.
	 */
	protected static Material globalMaterial = null;

	/** List of matrix modes to update with the objects transforms */
	protected static int[] matrixUpdateList = NORMAL_UPDATE;

	/**
	 * Count of all the instance of Hierarchical objects created so far. Used to
	 * create unique names.
	 */
	protected static int uniqueIDCount = 0;

	/** The material of this object */
	private Material material = new Lambertian();

	/** The transform of this object (split up into scale, rotation and translate parts) */
	private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
	private Quat4f rotation = new Quat4f(0.0f, 0.0f, 0.0f, 1.0f);
	private Vector3f translate = new Vector3f(0.0f, 0.0f, 0.0f);
	
	/** Matrix version of the above - generated in updateObjectTransform() */
	private Matrix4f objectTransform = new Matrix4f();

	/** Object transform * Parent's world transform - gets updated in glRender */
	protected Matrix4f worldTransform = new Matrix4f();

	/** Bounding sphere in local space (always centered at (0, 0, 0) */
	protected BoundingSphere boundingSphere = new BoundingSphere();
	
	protected BoundingBox boundingBox = new BoundingBox();
	
	/** The parent of this node */
	private HierarchicalObject parent = null;

	/** The children of this node */
	private final Vector<HierarchicalObject> children = new Vector<HierarchicalObject>();

	/** The name of this object */
	protected String name;

	/**haha
	 * Should be used only be the Parser. Please name your objects!
	 */
	public HierarchicalObject() {
		this("Object " + getUniqueID());
		updateObjectTransform();
	}

	/**
	 * Initializes the object transform to the identity matrix
	 */
	public HierarchicalObject(String inName) {
		setTransformToIdentity();
		name = inName;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Drawing
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The draw command for this object. This method assumes that the local
	 * transformation has been applied and the material has been set.
	 * 
	 * @param gl
	 * @param glu
	 * @param eye
	 */
	protected abstract void draw(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException;

	public boolean boxInFrustum() 
	{
		boolean result = true;
		//for each plane do ...
		for(int i=0; i < 6; i++) {

			// is the positive vertex outside?
			if (Camera.fPlane[i].distance(boundingBox.getVertexP(Camera.fPlane[i].getNormal())) < 0)
				return false;
			// is the negative vertex outside?	
			else if (Camera.fPlane[i].distance(boundingBox.getVertexN(Camera.fPlane[i].getNormal())) < 0)
				result =  true;
		}
		return result;
	 }
	
	/**
	 * The main recursive rendering method for hierarchical objects. This method
	 * sets the current transforms, configures the material, draws the current
	 * object, and then recursively renders the children.
	 * 
	 * @param gl
	 * @param glu
	 * @param eye
	 */
	public final void glRender(GL gl, GLU glu, Vector3f eye) throws GLSLErrorException {
		for (int m : matrixUpdateList) {
			if (m >= GL.GL_TEXTURE0
					&& m < (GL.GL_TEXTURE0 + Math.max(GL.GL_MAX_TEXTURE_COORDS,
							GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS))) {
				gl.glActiveTexture(m);
				gl.glMatrixMode(GL.GL_TEXTURE);
			} else {
				gl.glMatrixMode(m);
			}			
			gl.glPushMatrix();
			gl.glMultMatrixf(GLUtils.fromMatrix4f(objectTransform), 0);
		}

		/* Maintain a concatenated world transform (needed for linear skinning) */
		if (getParent() == null) {
			worldTransform.set(objectTransform);
		} else {
			worldTransform.mul(((HierarchicalObject) getParent()).worldTransform, 
					objectTransform);
		}				

		configMaterial(gl, eye);
		draw(gl, glu, eye);
	
		// Draw the children
		for (HierarchicalObject currChild : children) {
			currChild.glRender(gl, glu, eye);
		}
					

		for (int m : matrixUpdateList) {
			if (m >= GL.GL_TEXTURE0
					&& m < (GL.GL_TEXTURE0 + Math.max(GL.GL_MAX_TEXTURE_COORDS,
							GL.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS))) {
				gl.glActiveTexture(m);
				gl.glMatrixMode(GL.GL_TEXTURE);
			} else {
				gl.glMatrixMode(m);
			}
			gl.glPopMatrix();	
		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Materials
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Configures the current material for rendering.
	 */
	public void configMaterial(GL gl, Vector3f eye) throws GLSLErrorException {
		if (globalMaterial != null) {
			globalMaterial.glSetMaterial(gl, eye);
		} else {
			material.glSetMaterial(gl, eye);
		}
	}

	/**
	 * Sets the global material. When set, the global material overides all
	 * local materials.
	 * 
	 * @param inGlobal
	 */
	public static synchronized void setGlobalMaterial(Material inGlobal) {
		globalMaterial = inGlobal;
	}

	/**
	 * Removes the global material and resumes use of the local materials.
	 */
	public static synchronized void unSetGlobalMaterial() {
		globalMaterial = null;
	}

	/**
	 * Returns the material for this object
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Sets the material for this object. If the object has children, they are
	 * set recursively.
	 * 
	 * @param in:
	 *            Material to set
	 */
	public void setMaterial(Material in) {
		material = in.copy();
		for (HierarchicalObject currChild : children) {
			currChild.setMaterial(in);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Naming
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Return the next uniqueID.
	 * 
	 * @return
	 */
	protected static synchronized int getUniqueID() {
		return uniqueIDCount++;
	}

	/**
	 * Returns the name of this object
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this object
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// I/O
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds an object to the end of the child list. Used by Parser
	 * 
	 * @param inObject
	 */
	public void addObject(HierarchicalObject inObject) {
		insert(inObject, getChildCount());
	}

	/**
	 * Write the local data for this object. The material and the open and close
	 * tags will be written automattically. The indention already reflects the
	 * indent for the current tag.
	 * 
	 * @param out
	 * @param indent
	 */
	protected abstract void writeLocalData(PrintStream out, int indent);

	/**
	 * Write this object to the given PrintStream at the given indention.
	 * 
	 * @param out
	 * @param indent
	 */
	public void write(PrintStream out, int indent) {
		out.println(WritingUtils.getTagOpen("object type=\""
				+ this.getClass().getName() + "\"", indent));
		material.write(out, indent + WritingUtils.INDENTION_STEP);
		WritingUtils.writeTuple3f(out, translate, "translate", indent);
		WritingUtils.writeTuple3f(out, scale, "scale", indent);
		WritingUtils.writeTuple4f(out, rotation, "rotation", indent);
		WritingUtils.writeString(out, name, "name", indent
				+ WritingUtils.INDENTION_STEP);
		writeLocalData(out, indent + WritingUtils.INDENTION_STEP);
		for (HierarchicalObject currObj : children) {
			currObj.write(out, indent + WritingUtils.INDENTION_STEP);
		}
		out.println(WritingUtils.getTagClose("object", indent));
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Transform manipulations
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Return the object's transformation matrix
	 */
	public Matrix4f getObjectTransform() {
		return objectTransform;
	}
	
	/**
	 * Return the object's world space transformation matrix
	 * (concatenated object transforms of all parents + local object transform)
	 */
	public Matrix4f getWorldTransform() {
		return worldTransform;
	}
	
	/**
	 * Regenerates the concatenated object transform from the scale, rotation
	 * and translate values (in that order).
	 */
	public void updateObjectTransform() {
		Matrix4f rotPart = new Matrix4f();
		rotPart.set(rotation);
		objectTransform.setZero();
		objectTransform.m00 = scale.x;
		objectTransform.m11 = scale.y;
		objectTransform.m22 = scale.z;
		objectTransform.m33 = 1.0f;
		objectTransform.mul(rotPart, objectTransform);
		objectTransform.m03 = translate.x;
		objectTransform.m13 = translate.y;
		objectTransform.m23 = translate.z;
	}

	/**
	 * Updates the object transform (translate part)
	 */
	public void setTranslate(Vector3f value) {
		translate.set(value);
		updateObjectTransform();
	}

	/**
	 * Updates the object transform (translate part)
	 */
	public void setTranslate(float x, float y, float z) {
		translate.set(x, y, z);
		updateObjectTransform();
	}

	/**
	 * Return the translate part of the object transform
	 */
	public Vector3f getTranslate() {
		return translate;
	}

	/**
	 * Updates the object transform (scale part)
	 */
	public void setScale(Vector3f value) {
		scale.set(value);
		updateObjectTransform();
	}

	/**
	 * Updates the object transform (scale part)
	 */
	public void setScale(float x, float y, float z) {
		scale.set(x, y, z);
		updateObjectTransform();
	}

	/**
	 * Return the scale part of the object transform
	 */
	public Vector3f getScale() {
		return scale;
	}

	/**
	 * Updates the object transform (rotational part)
	 */
	public void setRotation(Quat4f value) {
		rotation.set(value);
		updateObjectTransform();
	}

	/**
	 * Updates the object transform (rotational part)
	 */
	public void setRotationAxisAngle(AxisAngle4f value) {
		rotation.set(value);
		updateObjectTransform();
	}

	/**
	 * Updates the object transform (rotational part) with a rotation
	 * around (axisX, axisY, axisZ) by angle. The angle should be
	 * given in degrees.
	 */
	public void setRotationAxisAngle(float angle, float axisX, float axisY, float axisZ) {
		setRotationAxisAngle(new AxisAngle4f(axisX, axisY, axisZ, (float) Math.toRadians(angle)));
	}

	/**
	 * Prepend a multiplication transformation to the
	 * curent rotation
	 */
	public void mulRotation(float angle, float axisX, float axisY, float axisZ) {
		Quat4f quat = new Quat4f();
		quat.set(new AxisAngle4f(axisX, axisY, axisZ, (float) Math.toRadians(angle)));
		rotation.mul(rotation, quat);
		updateObjectTransform();
	}	
	
	/**
	 * Return the scale part of the object transform
	 */
	public Quat4f getRotation() {
		return rotation;
	}

	/**
	 * Sets this object's transform to the identity.
	 */
	public void setTransformToIdentity() {
		translate.x = translate.y = translate.z = 0.0f;
		scale.x = scale.y = scale.z = 1.0f;
		rotation.x = rotation.y = rotation.z = 0.0f;
		rotation.w = 1.0f; 
		updateObjectTransform();
	}


	/**
	 * Return the bounding sphere enclosing this object and all sub-objects.
	 * The sphere is in local coordinate system, e.g. it is always centered at
	 * (0, 0, 0)
	 */
	public BoundingSphere getBoundingSphere() {
		return boundingSphere;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	/**
	 * Updates the hierarchy of bounding spheres
	 */
	public void recursiveUpdateBoundingSpheres() {
		boundingSphere.reset();
		for (int i=0; i<getChildCount(); i++) {
			HierarchicalObject child = (HierarchicalObject) getChildAt(i);
			
			/* The bounding box of a skinned object should not be included.
			 * Instead, the skeleton will determine the correct bounding
			 * sphere size
			 */
			if (child instanceof SkinnedMeshObject)
				continue;

			child.recursiveUpdateBoundingSpheres();
			boundingSphere.expandBy(child.getBoundingSphere().transform(child.getObjectTransform()));
		}
	}

	public void recursiveUpdateBoundingBoxes() {
		//boundingSphere.reset();
		for (int i=0; i<getChildCount(); i++) {
			HierarchicalObject child = (HierarchicalObject) getChildAt(i);
			
			/* The bounding box of a skinned object should not be included.
			 * Instead, the skeleton will determine the correct bounding
			 * sphere size
			 */
			if (child instanceof SkinnedMeshObject)
				continue;

			
			child.recursiveUpdateBoundingBoxes();
			
			/*
			if (child.getChildCount() == 0 && child instanceof MeshObject)
			{
			  	System.out.println("here setting mesh vertices");
			  	MeshObject mo = (MeshObject) child;
			  	mo.
			} else
			{
			*/
			BoundingBox b = child.getBoundingBox();
			if (b.isInitialized())
			 boundingBox.expandBy(b);
			else
			 System.out.println("PROBLEM - box not initialized");
			
			//System.out.println("(Heirarch) " + name + ", " + boundingBox);
		}
	}

	/**
	 * Set the update list to the input values
	 * 
	 * @param updateList
	 */
	public synchronized static void setUpdateList(int[] updateList) {
		matrixUpdateList = updateList;
	}

	/**
	 * Resets the update list to normal operations
	 */
	public synchronized static void unSetUpdateList() {
		matrixUpdateList = NORMAL_UPDATE;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// MutableTreeNode interface
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode,
	 *      int)
	 */
	public void insert(MutableTreeNode child, int index) {
		child.setParent(this);
		children.insertElementAt((HierarchicalObject) child, index);
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#remove(int)
	 */
	public void remove(int index) {
		children.remove(index);
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#remove(javax.swing.tree.MutableTreeNode)
	 */
	public void remove(MutableTreeNode node) {
		children.remove(node);
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#removeFromParent()
	 */
	public void removeFromParent() {
		parent.children.remove(this);
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#setParent(javax.swing.tree.MutableTreeNode)
	 */
	public void setParent(MutableTreeNode newParent) {
		this.parent = (HierarchicalObject) newParent;
	}

	/**
	 * @see javax.swing.tree.MutableTreeNode#setUserObject(java.lang.Object)
	 */
	public void setUserObject(Object object) {
		throw new Error(
				"CS569.Objects.HierarchicalObject.setUserObject(): User objects not supported.");
	}

	/**
	 * @see javax.swing.tree.TreeNode#children()
	 */
	public Enumeration<? extends TreeNode> children() {
		return Collections.enumeration(children);
	}

	/**
	 * @see javax.swing.tree.TreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	/**
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	public int getChildCount() {
		return children.size();
	}

	/**
	 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
	 */
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	/**
	 * @see javax.swing.tree.TreeNode#getParent()
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return children.isEmpty();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Iterable
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<HierarchicalObject> iterator() {
		return children.iterator();
	}

	/**
	 * Find a child node by name
	 */
	public HierarchicalObject findByName(String name) {
		if (name.equals(getName()))
			return this;
		for (int i=0; i<getChildCount(); i++) {
			HierarchicalObject obj = ((HierarchicalObject) getChildAt(i)).findByName(name);
			if (obj != null)
				return obj;
		}
		return null;
	}
}