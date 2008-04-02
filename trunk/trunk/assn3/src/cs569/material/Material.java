package cs569.material;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.media.opengl.GL;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import cs569.misc.GLSLErrorException;
import cs569.misc.WritingUtils;

/**
 * Base class for Materials. Base class contains stubs for I/O functions and the
 * generation of GUI elements for the viewer.
 * 
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public abstract class Material {

	/** Emptry material used to disable shading */
	private static final Material PASS_THROUGH_MATERIAL = new PassThroughMaterial();

	/** The property panel for this material */
	protected JPanel propertyPanel = null;

	/**
	 * Return the emptry material.
	 * 
	 * @return
	 */
	public static final Material getPassThroughMaterial() {
		return PASS_THROUGH_MATERIAL;
	}

	/**
	 * Does this material need tangent space vectors?
	 * @return false
	 */
	public boolean needsTangentSpace() {
		return false;
	}
	
	public boolean needsSkin() {
		return false;		
	}
	
	/**
	 * Called whenever a material is just about to be used for rendering.
	 * Sub-classes should implement this method to set all of the input
	 * parameters the shader will need.
	 * 
	 * @param eye
	 * @throws GLSLErrorException
	 */
	abstract public void glSetMaterial(GL gl, Vector3f eye)
			throws GLSLErrorException;

	/**
	 * Used by the GUI. Must create a new deep copy (all internal fields must
	 * also be copied) of the material.
	 * 
	 * @return
	 */
	abstract public Material copy();

	/**
	 * Return an approximation of the diffuse reflectance of this surface. Used
	 * in later assignments. Default implementation throws an error.
	 * 
	 * @return
	 */
	public void getApproximateDiffuseColor(Color3f outColor) {
		throw new Error(this.getClass().getName() + ": Unsupported operation.");
	}

	/**
	 * Create the property panel for this material.
	 */
	public void createPropertyPanel() {
		propertyPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Empty.");
		label.setEnabled(false);
		propertyPanel.add(label, BorderLayout.CENTER);
	}

	/**
	 * Return the property panel for this material. Property panels are
	 * displayed in the lower right hand corner of the GUI when an object is
	 * selected.
	 * 
	 * @return
	 */
	public final JPanel getPropertyPanel() {
		if (propertyPanel == null) {
			createPropertyPanel();
		}
		return propertyPanel;
	}

	/**
	 * Writes the local data for this material. Assumes that the open and close
	 * tags will be written and that the indention has been set to accomodate.
	 * 
	 * @param out
	 * @param indent
	 */
	protected abstract void writeLocalData(PrintStream out, int indent);

	/**
	 * Write this material to the supplied PrintStream at the given indention
	 * level
	 * 
	 * @param out
	 * @param indent
	 */
	public void write(PrintStream out, int indent) {
		out.println(WritingUtils.getTagOpen("material type=\""
				+ this.getClass().getName() + "\"", indent));
		writeLocalData(out, indent + WritingUtils.INDENTION_STEP);
		out.println(WritingUtils.getTagClose("material", indent));
	}

	/**
	 * Material that does nothing. Turning off shading.
	 * 
	 * @author Adam Jan 26, 2007 Material.java Copyright 2005 Program of
	 *         Computer Graphics, Cornell University
	 */
	private static class PassThroughMaterial extends Material {

		/**
		 * @see cs569.material.Material#copy()
		 */
		@Override
		public Material copy() {
			return new PassThroughMaterial();
		}

		/**
		 * @see cs569.material.Material#glSetMaterial(javax.media.opengl.GL,
		 *      javax.vecmath.Vector3f)
		 */
		@Override
		public void glSetMaterial(GL gl, Vector3f eye)
				throws GLSLErrorException {
		}

		/**
		 * @see cs569.material.Material#writeLocalData(java.io.PrintStream, int)
		 */
		@Override
		protected void writeLocalData(PrintStream out, int indent) {
		}
	}
}
