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
	
	/** The normalTmp coordinate array */
	protected FloatBuffer normalsTmp;

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
	private void extractNormal(int index, Vector3f v)
	{
		int tri = triangles.get(index);
		v.set(normals.get(3*tri),normals.get(3*tri+1),normals.get(3*tri+2));
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
	
	/**
	 * Calculate the tangent space vectors
	 */
	public void calculateTangentSpace() {
		
		triangles.rewind();
		verts.rewind();
		texcoords.rewind();
		normals.rewind();
		
		tangents = BufferUtil.newFloatBuffer(verts.capacity());
		binormals = BufferUtil.newFloatBuffer(verts.capacity());
		normalsTmp = BufferUtil.newFloatBuffer(verts.capacity());
		
		Point3f P0 = new Point3f(); // assign TBN vals to P0, normals don't match up though :(
		Point3f P1 = new Point3f();
		Point3f P2 = new Point3f();
		
		Point2f uv0 = new Point2f();
		Point2f uv1 = new Point2f();
		Point2f uv2 = new Point2f();

		Vector3f n0 = new Vector3f();
		Vector3f n1 = new Vector3f();
		Vector3f n2 = new Vector3f();
		
		Vector3f Pa = new Vector3f();
		Vector3f Pb = new Vector3f();
		
		Vector2f Ta = new Vector2f();
		Vector2f Tb = new Vector2f();
		
		Vector3f Norm = new Vector3f();
		Vector3f Tang = new Vector3f();
		Vector3f Binorm = new Vector3f();

		for (int i=0; i<triangles.capacity()/3; i++)
		{
			extractPoint(3*i, P0);
			extractPoint(3*i+1, P1);
			extractPoint(3*i+2, P2);
			
			extractTexCoord(3*i, uv0);
			extractTexCoord(3*i+1, uv1);
			extractTexCoord(3*i+2, uv2);
			
			extractNormal(3*i, n0);
			extractNormal(3*i+1, n1);
			extractNormal(3*i+2, n2);
			
			Pa.sub(P1, P0);
			Pb.sub(P2, P0);
			
			Ta.sub(uv1, uv0);
			Tb.sub(uv2, uv0);
		
			computeFaceTBNBasis(Pa, Pb, Ta, Tb, Tang, Binorm, Norm);
			
			// Gram-Schmidt orthogonalization
			Vector3f gsNorm = new Vector3f(Norm);
			gsNorm.scale(Norm.dot(Tang));
			Tang.sub(gsNorm);
			Tang.normalize();
			
			
			
			// special circumstances
			if(hasNaN(Tang) || hasNaN(Norm) || hasNaN(Binorm))
			{ // handles top
				Tang.cross(Binorm, n0); // be sensitive to cases when binorm might be NaN
				Norm.set(n0);
				if((Binorm.dot(n0) < 0)) { // handles triangle at top at seam
					Binorm.scale(-1.0f);
					Tang.scale(-1.0f);
				}
			} else if (Norm.dot(n0) < 0) { // handles textures boundary
				Binorm.scale(-1.0f);
				Norm.scale(-1.0f);
			}
			
			/* Possibly useful from french site, looks like it does similar process
			 * as that handled below in special circumstances.
			 * //Right handed TBN space ?*/
			Vector3f tmp = new Vector3f();
			tmp.cross(Tang, Binorm);
			boolean rightHanded = tmp.dot(Norm) >= 0;
			Binorm.cross(Norm, Tang);
			if(!rightHanded)
			    Binorm.scale(-1.0f);
			
			Tang.normalize();
			Binorm.normalize();
			Norm.normalize();
			
			putBTNvalues(3*i, Binorm, Tang, Norm);
			putBTNvalues(3*i+1, Binorm, Tang, Norm);
			putBTNvalues(3*i+2, Binorm, Tang, Norm);
		}
		
		normalizeBTNSpaceVectors();
		normalsTmp.rewind();
		for(int i=0; i<normals.capacity(); i++)
		{
			normals.put(i, normalsTmp.get(i));
		}
	}
	
	void normalizeBTNSpaceVectors()
	{
		Vector3f tmp = new Vector3f();
		for(int i=0; i<tangents.capacity()/3; i++)
		{
			tmp.x = tangents.get(3*i);
			tmp.y = tangents.get(3*i+1);
			tmp.z = tangents.get(3*i+2);
			tmp.normalize();
			tangents.put(3*i, tmp.x);
			tangents.put(3*i+1, tmp.y);
			tangents.put(3*i+2, tmp.z);
			
			tmp.x = binormals.get(3*i);
			tmp.y = binormals.get(3*i+1);
			tmp.z = binormals.get(3*i+2);
			tmp.normalize();
			binormals.put(3*i, tmp.x);
			binormals.put(3*i+1, tmp.y);
			binormals.put(3*i+2, tmp.z);
			
			tmp.x = normalsTmp.get(3*i);
			tmp.y = normalsTmp.get(3*i+1);
			tmp.z = normalsTmp.get(3*i+2);
			tmp.normalize();
			normalsTmp.put(3*i, tmp.x);
			normalsTmp.put(3*i+1, tmp.y);
			normalsTmp.put(3*i+2, tmp.z);
			
		}
	
	}
	
	boolean hasNaN(Vector3f v)
	{
		return Float.isNaN(v.x) || Float.isNaN(v.y) || Float.isNaN(v.z);
	}
	
	boolean isZeroVector(Vector3f v)
	{
		return (v.x == 0.0f) && (v.y == 0.0f) && (v.z == 0.0f);
	}
	
	void putBTNvalues(int i, Vector3f B, Vector3f T, Vector3f N)
	{
		int tri = triangles.get(i);
		
		tangents.put(3*tri, tangents.get(3*tri)+T.x);
		tangents.put(3*tri+1, tangents.get(3*tri+1)+T.y);
		tangents.put(3*tri+2, tangents.get(3*tri+2)+T.z);
		
		binormals.put(3*tri, binormals.get(3*tri)+B.x);
		binormals.put(3*tri+1, binormals.get(3*tri+1)+B.y);
		binormals.put(3*tri+2, binormals.get(3*tri+2)+B.z);
		
		normalsTmp.put(3*tri, normalsTmp.get(3*tri)+N.x);
		normalsTmp.put(3*tri+1,normalsTmp.get(3*tri+1)+N.y);
		normalsTmp.put(3*tri+2, normalsTmp.get(3*tri+2)+N.z);
		
	}

	public static void computeFaceTBNBasis(Vector3f Pa, Vector3f Pb, Vector2f Ta, Vector2f Tb, 
			Vector3f tangent, Vector3f binormal, Vector3f normal)
	{
	    Vector3f p21  = Pa;  //p2-p1
	    Vector3f p31  = Pb;  //p3-p1
	    Vector2f uv21 = Ta;  //uv2-uv1
	    Vector2f uv31 = Tb;  //uv3-uv1

	    if(tangent != null || normal != null)
	    {
	    	Vector3f vec1 = new Vector3f(p31);
	    	vec1.scale(uv21.getY());
	    	Vector3f vec2 = new Vector3f(p21);
	    	vec2.scale(uv31.getY());
	    	vec2.sub(vec1);
	    	vec2.normalize();
	    	tangent.set(vec2);
	    }
	    if(binormal != null || normal != null)
	    {
	    	Vector3f vec1 = new Vector3f(p21);
	    	vec1.scale(uv31.getX());
	    	Vector3f vec2 = new Vector3f(p31);
	    	vec2.scale(uv21.getX());
	    	vec2.sub(vec1);
	    	vec2.normalize();
	    	binormal.set(vec2);
	    }
	    if(normal != null)
	    {    
	    	normal.cross(tangent, binormal);
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
