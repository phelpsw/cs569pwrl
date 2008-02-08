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
	
	private void solveTex2PointMatrix(Vector3f Pa, Vector3f Pb, Vector2f Ta, Vector2f Tb, GMatrix M)
	{
		M.setElement(0, 0, (Pa.x*Tb.y - Pb.x*Tb.y) / (-Tb.x*Ta.y + Ta.x*Tb.y)); // a
		M.setElement(0, 1, (Pa.x - M.getElement(0, 0)*Ta.x)/Ta.y); // b
		
		M.setElement(1, 0, (Pa.y*Tb.y - Pb.y*Tb.y) / (-Tb.x*Ta.y + Ta.x*Tb.y)); // c
		M.setElement(1, 1, (Pa.y - M.getElement(1, 0)*Ta.x)/Ta.y); // d
		
		M.setElement(2, 0, (Pa.z*Tb.y - Pb.z*Tb.y) / (-Tb.x*Ta.y + Ta.x*Tb.y)); // e
		M.setElement(2, 1, (Pa.z - M.getElement(2, 0)*Ta.x)/Ta.y); // f
	}
	
	private void convertTex2Point(Vector2f T, GMatrix M, Vector3f P)
	{
		GVector Tex = new GVector(T);
		GVector Pt = new GVector(3);
		Pt.mul(M, Tex);
		P.set((float)Pt.getElement(0), (float)Pt.getElement(1), (float)Pt.getElement(2));
	}
	
	/**
	 * Calculate the tangent space vectors
	 */
	public void calculateTangentSpace() {
		
		triangles.rewind();
		verts.rewind();
		texcoords.rewind();
		normals.rewind();
		
		tangents = BufferUtil.newFloatBuffer(triangles.capacity());
		binormals = BufferUtil.newFloatBuffer(triangles.capacity());
		
		Point3f P0 = new Point3f();
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
		
		GMatrix M = new GMatrix(3,2); // 3x2 Matrix
		
		Vector3f Norm = new Vector3f();
		Vector3f Tang = new Vector3f();
		Vector3f Binorm = new Vector3f();

		
		//for(int i=0; i<this.numTriangles*3; i+=3)
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
			
			
			//solveTex2PointMatrix(Pa, Pb, Ta, Tb, M);
			
			System.out.println("P0=" + P0 + ", P1="+P1+", P2="+P2);
			System.out.println("uv0=" + uv0 + ", uv1="+uv1+", uv2="+uv2);
			/*
			System.out.println("Ta:"+Ta);
			System.out.println("Tb:"+Tb);
			System.out.println("M:"+M);
			System.out.println("Pa:"+Pa);
			System.out.println("Pb:"+Pb);
			
			convertTex2Point(new Vector2f(1.0f, 0.00001f), M, Tang);
			
			convertTex2Point(new Vector2f(0.00001f, 1.0f), M, Binorm);
			
			Norm.cross(Tang, Binorm);
			*/
			
			
			computeFaceTBNBasis(Pa, Pb, Ta, Tb, Tang, Binorm, Norm);
			/*
			//Gram-Schmitt orthogonalization
			Vector3f gsNorm = new Vector3f(Norm);
			gsNorm.scale(gsNorm.dot(Tang));
			Tang.sub(gsNorm);
			Tang.normalize();
			*/
			/*
			//Right handed TBN space ?
			Vector3f tbCross = new Vector3f();
			tbCross.cross(Tang, Binorm);
			boolean rightHanded = tbCross.dot(Norm) >= 0;
		    Binorm.cross(Norm, Tang);
			if(!rightHanded)
				Binorm.scale(-1.0f);
		    */
		    //boolean rightHanded = dotProduct(crossProduct(tangent, binormal), normal) >= 0;
		    //binormal = crossProduct(normal, tangent);
		    //if(!rigthHanded)
		    //    binormal.multiply(-1);
			
			//Tang.subtract(multiply(normal, dotProduct(normal, tangent))).normalize();
			
			Tang.normalize();
			Binorm.normalize();
			Norm.normalize();
			
			System.out.println("Tang: "+Tang);
			System.out.println("Binorm: "+Binorm);
			System.out.println("Norm: "+Norm);
			System.out.println("MeshNorm: "+n0+" "+n1+" "+n2);
			System.out.println("----");
			
			tangents.put(Tang.x);
			tangents.put(Tang.y);
			tangents.put(Tang.z);
			
			binormals.put(Binorm.x);
			binormals.put(Binorm.y);
			binormals.put(Binorm.z);
		}
	}

	public static void computeFaceTBNBasis(Vector3f Pa, Vector3f Pb, Vector2f Ta, Vector2f Tb, 
			Vector3f tangent, Vector3f binormal, Vector3f normal)
	{
	    Vector3f p21  = Pa;  //p2-p1
	    Vector3f p31  = Pb;  //p3-p1
	    Vector2f uv21 = Ta;       //uv2-uv1
	    Vector2f uv31 = Tb;       //uv3-uv1

	    /*float f = uv21.getX()*uv31.getY() - uv21.getY()*uv31.getX();
	    f = (f == 0) ? 1 : 1 / f;*/

	    //Note: As vectors are normalized, we can skip the 1/f division and the normalization of the normal
	    //      Feel free to keep/enable the commented code.
	    if(tangent != null || normal != null)
	    {
	    	Vector3f vec1 = new Vector3f(p31);
	    	vec1.scale(uv21.getY());
	    	Vector3f vec2 = new Vector3f(p21);
	    	vec2.scale(uv31.getY());
	    	vec2.sub(vec1);
	    	vec2.normalize();
	    	tangent.set(vec2);
	    	
	        //tangent.copyFrom( normalize(/*multiply(*/multiply(p21, uv31.getY()).subtract(multiply(p31, uv21.getY()))/*, f)*/));
	    
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
	    	
	        //binormal.copyFrom(normalize(/*multiply(*/multiply(p31, uv21.getX()).subtract(multiply(p21, uv31.getX()))/*, f)*/));
	    }
	    if(normal != null)
	    {    
	    	normal.cross(tangent, binormal);
	    	//normal.copyFrom( /*normalize(*/crossProduct(tangent, binormal)/*)*/);
	    
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
