package cs569.object;

import java.io.PrintStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point2f;
import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

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

	private void extractPoint(int index, Point3f p)
	{
		int tri = triangles.get(index);
		p.set(verts.get(3*tri),verts.get(3*tri+1),verts.get(3*tri+2));
	}
	
	private void extractTexCoord(int index, Point2f p)
	{
		int tri = triangles.get(index);
		p.set(texcoords.get(2*tri),texcoords.get(2*tri+1));
	}
	
	private void solveTex2PointMatrix(Vector3f Pa, Vector3f Pb, Vector2f Ta, Vector2f Tb, GMatrix M)
	{
		M.setElement(0, 0, (Pb.x*Ta.y - Tb.y*Pa.x) / (Tb.x*Ta.y - Ta.x*Tb.y)); // a
		M.setElement(0, 1, (Pa.x - M.getElement(0, 0)*Ta.x)/Ta.y); // b
		
		M.setElement(1, 0, (Pa.y*Tb.y - Ta.y*Pb.y) / (Ta.x*Tb.y - Tb.x*Ta.y)); // c
		M.setElement(1, 1, (Pa.y - M.getElement(1, 0)*Ta.x)/Ta.y); // d
		
		M.setElement(2, 0, (Tb.y*Pa.z - Pb.z*Ta.y) / (Ta.x*Tb.y - Tb.x*Ta.y)); // e
		M.setElement(2, 1, (Pa.z - M.getElement(2, 0)*Ta.x)/Ta.y); // f
	}
	
	private void convertTex2Point(Vector2f T, GMatrix M, Vector3f P)
	{
		GVector Tex = new GVector(T);
		GVector Pt = new GVector(3);
		Pt.mul(M, Tex);
		P.set((float)Pt.getElement(0), (float)Pt.getElement(1), (float)Pt.getElement(2));
	}
	
	private void convertPoint2Tex(Vector3f P, GMatrix M, Vector2f T)
	{
		GVector Tex = new GVector(2);
		GVector Pt = new GVector(P);
		GMatrix Minv = new GMatrix(M);
		Minv.invert();
		Tex.mul(Minv, Pt);
		T.set((float)Pt.getElement(0), (float)Pt.getElement(1));
	}
	
	/**
	 * Calculate the tangent space vectors
	 */
	public void calculateTangentSpace() {
		
		tangents = BufferUtil.newFloatBuffer(triangles.capacity());
		binormals = BufferUtil.newFloatBuffer(triangles.capacity());
		
		Point3f P0 = new Point3f();
		Point3f P1 = new Point3f();
		Point3f P2 = new Point3f();
		
		Point2f uv0 = new Point2f();
		Point2f uv1 = new Point2f();
		Point2f uv2 = new Point2f();
		
		Vector3f Pa = new Vector3f();
		Vector3f Pb = new Vector3f();
		
		Vector2f Ta = new Vector2f();
		Vector2f Tb = new Vector2f();
		
		GMatrix M = new GMatrix(3,2); // 3x2 Matrix
		
		Vector3f Norm = new Vector3f();
		Vector3f Tang = new Vector3f();
		Vector3f Binorm = new Vector3f();

		for(int i=0; i<triangles.capacity()/3; i++)
		{
			extractPoint(i, P0);
			extractPoint(i+1, P1);
			extractPoint(i+2, P2);
			
			extractTexCoord(i, uv0);
			extractTexCoord(i+1, uv1);
			extractTexCoord(i+2, uv2);
			
			Pa.sub(P1, P0);
			Pb.sub(P2, P0);
			
			Ta.sub(uv1, uv0);
			Tb.sub(uv2, uv0);
			
			solveTex2PointMatrix(Pa, Pb, Ta, Tb, M);
			
			convertTex2Point(new Vector2f(1.0f, 0.0f), M, Tang);
			
			convertTex2Point(new Vector2f(0.0f, 1.0f), M, Binorm);
			
			Norm.cross(Tang, Binorm);
			
			Tang.normalize();
			Binorm.normalize();
			Norm.normalize();
			
			tangents.put(Tang.x);
			tangents.put(Tang.y);
			tangents.put(Tang.z);
			
			binormals.put(Binorm.x);
			binormals.put(Binorm.y);
			binormals.put(Binorm.z);
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
