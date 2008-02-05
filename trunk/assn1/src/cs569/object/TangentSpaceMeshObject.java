package cs569.object;

import java.io.PrintStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;

import cs569.misc.WritingUtils;

/**
 * Extends the basic MeshObject with tangent space generation
 * and support for supplying pixel/vertex shaders with this information

 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class TangentSpaceMeshObject extends MeshObject {
	/** The tangent coordinate array */
	protected FloatBuffer tangents;

	/** The binormal coordinate array */
	protected FloatBuffer binormals;

	/** Shader attribute handle for tangent vectors */
	protected int tangentHandle = -1;

	/** Shader attribute handle for binormal vectors */
	protected int binormalHandle = -1;

	/**
	 * Should be used only be the Parser. Please name your objects!
	 */
	public TangentSpaceMeshObject() {
	}

	/**
	 * Default constructor.
	 * 
	 * @param inName
	 */
	public TangentSpaceMeshObject(String inName) {
		super(inName);
	}

	/**
	 * Basic constructor. Sets mesh data array into the mesh structure.
	 * IMPORTANT: The data array are not copies so changes to the input data
	 * array will affect the mesh structure. The number of vertices and the
	 * number of triangles are inferred from the lengths of the verts and tris
	 * array. If either is not a multiple of three, an error is thrown.
	 * 
	 * @param verts
	 *            the vertex data
	 * @param tris
	 *            the triangle data
	 * @param normals
	 *            the normal data
	 * @param texcoords
	 *            the texture coordinate data
	 */
	public TangentSpaceMeshObject(float[] verts, int[] tris, float[] normals,
			float[] texcoords, String inName) {
		super(verts, tris, normals, texcoords, inName);
	}

	/**
	 * Calculate the tangent space vectors
	 */
	public void calculateTangentSpace() {
		/// to be implemented
	}

	
	/**
	 * Verify that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly.
	 * To be extended in subclasses!
	 */
	@Override
	protected boolean isConfiguredForShader(GL gl) {
		if (!super.isConfiguredForShader(gl))
			return false;

		if (getMaterial().needsTangentSpace()) {
			if (tangents == null && binormals == null)
				return false;
			if (binormalHandle == -1 || tangentHandle == -1)
				return false;
		}
		return true;
	}

	/**
	 * Ensure that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly.
	 * To be extended in subclasses!
	 */
	@Override
	protected void configureForShader(GL gl) {
		super.configureForShader(gl);

		if (getMaterial().needsTangentSpace()) {
			if (tangents == null && binormals == null) {
				calculateTangentSpace();
			}

			if (program != 0) {
				tangentHandle = gl.glGetAttribLocation(program, "tangent");
				binormalHandle = gl.glGetAttribLocation(program, "binormal");
			}
		}
	}

	/**
	 * Pass on tangent space vectors to the shader
	 */
	@Override
	protected void connectToShader(GL gl) {
		super.connectToShader(gl);
		if (getMaterial().needsTangentSpace()) {
			gl.glEnableVertexAttribArray(tangentHandle);
			gl.glEnableVertexAttribArray(binormalHandle);
			tangents.rewind(); binormals.rewind();
			gl.glVertexAttribPointer(tangentHandle, 3, GL.GL_FLOAT, false, 0, tangents);
			gl.glVertexAttribPointer(binormalHandle, 3, GL.GL_FLOAT, false, 0, binormals);
		}
	}

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 */
	@Override
	protected void disconnectFromShader(GL gl) {
		super.disconnectFromShader(gl);
		if (getMaterial().needsTangentSpace()) {
			gl.glDisableVertexAttribArray(tangentHandle);
			gl.glDisableVertexAttribArray(binormalHandle);
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param binormals
	 */
	public void setBinormals(double[] binormals) {
		if (binormals.length % 3 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setBinormals(): Biormal array length is not a multiple of three.");
		}
		this.binormals = copyIntoNewBuffer(binormals);
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param tangents
	 */
	public void setTangents(double[] tangents) {
		if (tangents.length % 3 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setTangents(): Tangent array length is not a multiple of three.");
		}
		this.tangents = copyIntoNewBuffer(tangents);
	}

	/**
	 * @see cs569.object.HierarchicalObject#writeLocalData(java.io.PrintStream,
	 *      int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		super.writeLocalData(out, indent);
		if (tangents != null)
			WritingUtils.writeFloatBuffer(out, tangents, "tangents", indent);
		if (binormals != null)
			WritingUtils.writeFloatBuffer(out, binormals, "binormals", indent);
	}
}
