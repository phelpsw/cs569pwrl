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
	 * Helper function for calculateTangentSpace() - adds
	 * a vector to an existing vector inside a float buffer
	 */
	private void addVector3(FloatBuffer buf, int pos, Vector3f vec) {
		float x = buf.get(pos*3+0), y = buf.get(pos*3+1), z = buf.get(pos*3+2);
		buf.put(pos*3+0, vec.x+x); buf.put(pos*3+1, vec.y+y); buf.put(pos*3+2, vec.z+z);
	}

	/**
	 * Helper function for calculateTangentSpace() - sets
	 * a vector inside a float buffer
	 */
	private void putVector3(FloatBuffer buf, int pos, Vector3f vec) {
		buf.put(pos*3+0, vec.x); buf.put(pos*3+1, vec.y); buf.put(pos*3+2, vec.z);
	}
	
	/**
	 * Helper function for calculateTangentSpace() - returns
	 * a Vector3f from a float buffer
	 */
	private Vector3f getVector3(FloatBuffer buf, int pos) {
		return new Vector3f(
			buf.get(pos*3+0), buf.get(pos*3+1), buf.get(pos*3+2)
		);
	}

	/**
	 * Helper function for calculateTangentSpace() - returns
	 * a Vector2f from a float buffer
	 */
	private Vector2f getVector2(FloatBuffer buf, int pos) {
		return new Vector2f(
			buf.get(pos*2+0), buf.get(pos*2+1)
		);
	}

	/**
	 * Calculate the tangent space vectors
	 */
	public void calculateTangentSpace() {
		boolean calculateNormals = false;

		if (texcoords == null)
			return;
		this.tangents = BufferUtil.newFloatBuffer(numVertices * 3);
		this.binormals = BufferUtil.newFloatBuffer(numVertices * 3);

		if (calculateNormals)
			normals.clear();

		/* Reset to zero */
		for (int i=0; i<numVertices*3; i++) {
			tangents.put(0);
			binormals.put(0);
			if (calculateNormals)
				normals.put(0);
		}

		for (int i=0; i<numTriangles; i++) {
			/* Retrieve all required triangle data */
			int index0 = triangles.get(i*3),
				index1 = triangles.get(i*3+1),
				index2 = triangles.get(i*3+2);
			Vector3f v0 = getVector3(verts, index0),
					 v1 = getVector3(verts, index1),
					 v2 = getVector3(verts, index2);
			Vector2f uv0 = getVector2(texcoords, index0),
					 uv1 = getVector2(texcoords, index1),
					 uv2 = getVector2(texcoords, index2);

			/* Calculate the derivatives */
			Vector3f dP1 = new Vector3f(v1), dP2 = new Vector3f(v2);
			dP1.sub(v0); dP2.sub(v0);
			Vector2f dUV1 = new Vector2f(uv1), dUV2 = new Vector2f(uv2);
			dUV1.sub(uv0); dUV2.sub(uv0);

			/* Calculate the tangent vector */
			Vector3f tangent = new Vector3f();
			tangent.scaleAdd(dUV2.y, dP1, tangent);
			tangent.scaleAdd(-dUV1.y, dP2, tangent);

			
			/* Calculate the binormal vector */
			Vector3f binormal = new Vector3f();
			binormal.scaleAdd(dUV2.x, dP1, binormal);
			binormal.scaleAdd(-dUV1.x, dP2, binormal);

			/* Account for mesh anomalies */
			if (tangent.length() == 0 || binormal.length() == 0) {
				Vector3f normal = new Vector3f();
				normal.add(getVector3(normals, index0));
				normal.add(getVector3(normals, index1));
				normal.add(getVector3(normals, index2));
				normal.normalize();
				if (tangent.length()==0)
					tangent.cross(normal, binormal);
				else
					binormal.cross(tangent, normal);
			}

			tangent.normalize();
			binormal.normalize();

			if (calculateNormals) {
				Vector3f normal = new Vector3f();
				normal.cross(dP1, dP2);
				normal.normalize();
				addVector3(normals, index0, normal);
				addVector3(normals, index1, normal);
				addVector3(normals, index2, normal);
			}
			
			/* Accumulate these vectors per vertex */
			addVector3(tangents, index0, tangent);
			addVector3(tangents, index1, tangent);
			addVector3(tangents, index2, tangent);
			addVector3(binormals, index0, binormal);
			addVector3(binormals, index1, binormal);
			addVector3(binormals, index2, binormal);
		}
		
		/* Re-normalization + Gram-Schmidt orthogonalization pass */
		for (int i=0; i<numVertices; i++) {
			Vector3f normal = getVector3(normals, i);
			Vector3f tangent = getVector3(tangents, i);
			Vector3f binormal = getVector3(binormals, i);

			if (calculateNormals)
				normal.normalize();

			tangent.scaleAdd(-tangent.dot(normal), normal, tangent);
			tangent.normalize();

			binormal.scaleAdd(-binormal.dot(normal), normal, binormal);
			binormal.scaleAdd(-binormal.dot(tangent), tangent, binormal);
			binormal.normalize();

			/* Check for correct coordinate system handedness */
			Vector3f computedNormal = new Vector3f();
			computedNormal.cross(binormal, tangent);
			if (computedNormal.dot(normal) < 0)
				binormal.scale(-1);

			putVector3(tangents, i, tangent);
			putVector3(binormals, i, binormal);

			if (calculateNormals)
				putVector3(normals, i, normal);
		}
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
		}
	}

	/**
	 * Pass on the tangent vectors to the shader
	 */
	@Override
	protected void connectToShader(GL gl) {
		super.connectToShader(gl);
		if (getMaterial().needsTangentSpace()) {
			gl.glEnableClientState(GL.GL_COLOR_ARRAY);
			tangents.rewind();
			gl.glColorPointer(3, GL.GL_FLOAT, 0, tangents);
		}
	}

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 */
	@Override
	protected void disconnectFromShader(GL gl) {
		super.disconnectFromShader(gl);
		if (getMaterial().needsTangentSpace()) {
			gl.glDisableClientState(GL.GL_COLOR_ARRAY);
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
