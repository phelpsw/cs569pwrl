package cs569.object;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;

import cs569.misc.BoundingSphere;
import cs569.misc.GroupData;
import cs569.misc.OBJLoader;
import cs569.misc.OBJLoaderException;
import cs569.misc.WritingUtils;

/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 * @author Wenzel Jakob (GLSL port + tangent space code)
 */
public class MeshObject extends HierarchicalObject {
	/** The number of vertices in the mesh */
	protected int numVertices;

	/** The number of triangles in the mesh */
	protected int numTriangles;

	/** The vertex array -- always present in each mesh */
	protected FloatBuffer verts;

	/** The texture coordinate array -- may be null */
	protected FloatBuffer texcoords;

	/** The normal coordinate array */
	protected FloatBuffer normals;

	/** Mesh triangle objects for each triangle. */
	protected IntBuffer triangles;

	/** The GPU program currently associated with this mesh */
	protected int program = 0;

	/** The bounding sphere comprising all mesh vertices */
	protected BoundingSphere meshBoundingSphere = null;

	/**
	 * Should be used only be the Parser. Please name your objects!
	 */
	public MeshObject() {
		super("Mesh " + getUniqueID());
	}

	/**
	 * Default constructor.
	 * 
	 * @param inName
	 */
	public MeshObject(String inName) {
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
	public MeshObject(float[] verts, int[] tris, float[] normals,
			float[] texcoords, String inName) {

		super(inName);

		// check the inputs
		if (verts.length % 3 != 0) {
			throw new Error(
					"Vertex array for a triangle mesh is not a multiple of 3.");
		}
		if (tris.length % 3 != 0) {
			throw new Error(
					"Triangle array for a triangle mesh is not a multiple of 3.");
		}

		// Set data
		setMeshData(verts, tris, normals, texcoords);

	}

	/**
	 * Sets the mesh data and builds the triangle array.
	 * 
	 * @param verts
	 *            the vertices
	 * @param tris
	 *            the triangles
	 * @param normals
	 *            the normals
	 * @param texcoords
	 *            the texture coordinates
	 */
	private void setMeshData(float[] verts, int[] tris, float[] normals,
			float[] texcoords) {

		this.verts = BufferUtil.newFloatBuffer(verts.length);
		this.normals = BufferUtil.newFloatBuffer(normals.length);
		this.texcoords = BufferUtil.newFloatBuffer(texcoords.length);
		this.triangles = BufferUtil.newIntBuffer(tris.length);

		this.numVertices = verts.length / 3;
		this.numTriangles = tris.length / 3;
		this.verts.put(verts);
		this.normals.put(normals);
		this.texcoords.put(texcoords);
		this.triangles.put(tris);
	}

	/**
	 * Calculate the bounding sphere comprising all mesh vertices
	 */
	@Override
	public void recursiveUpdateBoundingSpheres() {
		super.recursiveUpdateBoundingSpheres();
		
		if (meshBoundingSphere == null) {
			/* The bounding sphere of the mesh vertices has
			 * never been calculated
			 */
			meshBoundingSphere = new BoundingSphere();
			for (int i=0; i<numVertices*3; i+=3) {
				Point3f pt = new Point3f(verts.get(i), verts.get(i+1), verts.get(i+2));
				meshBoundingSphere.expandBy(pt);
			}
		}

		boundingSphere.expandBy(meshBoundingSphere);
	}
	
	public void recursiveUpdateBoundingBoxes() {
		//super.recursiveUpdateBoundingBoxes();
		
		if (boundingBox.isInitialized() == false) {
			/* The bounding sphere of the mesh vertices has
			 * never been calculated
			 */			
			for (int i=0; i<numVertices*3; i+=3) {
				Vector3f pt = new Vector3f(verts.get(i), verts.get(i+1), verts.get(i+2));
				boundingBox.expandBy(pt);
			}
			//System.out.println("RECURSIVE " + name + ", " + boundingBox);
		}
		
	}
	

	/**
	 * Verify that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly.
	 * To be extended in subclasses!
	 */
	protected boolean isConfiguredForShader(GL gl) {
		int program[] = new int[1];
		gl.glGetIntegerv(GL.GL_CURRENT_PROGRAM, program, 0);
		return (program[0] == this.program);
	}
	
	/**
	 * Ensure that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly.
	 * To be extended in subclasses!
	 */
	protected void configureForShader(GL gl) {
		int program[] = new int[1];
		gl.glGetIntegerv(GL.GL_CURRENT_PROGRAM, program, 0);
		this.program = program[0];		
	}

	/**
	 * Pass on mesh-related (e.g. not material-related) data to the shader
	 * To be extended in subclasses!
	 */
	protected void connectToShader(GL gl) {
	}

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 * To be extended in subclasses!
	 */
	protected void disconnectFromShader(GL gl) {
	}	
	
	/**
	 * Renders the object and all its children
	 */
	@Override
	public void draw(GL gl, GLU glu, Vector3f eye) {
		if (normals == null) {
			/* This is a good time to generate surface normals
			   if they are not part of the mesh */
			generateNormals();
		}
	
		verts.rewind();
		normals.rewind();
		triangles.rewind();

		gl.glVertexPointer(3, GL.GL_FLOAT, 0, verts);
		gl.glNormalPointer(GL.GL_FLOAT, 0, normals);
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		if (texcoords != null) {
			texcoords.rewind();
			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texcoords);
			gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		}

		boolean shaderReady = isConfiguredForShader(gl);
		if (!shaderReady) {
			configureForShader(gl);
			shaderReady = isConfiguredForShader(gl);
		}
		
		if (shaderReady)
			connectToShader(gl);


		gl.glDrawElements(GL.GL_TRIANGLES, 3 * numTriangles,
				GL.GL_UNSIGNED_INT, triangles);

		if (shaderReady)
			disconnectFromShader(gl);
	
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		if (texcoords != null) {
			gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Loading and Parsing
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Copies an array of doubles into a new FloatBuffer.
	 * 
	 * @return
	 */
	protected static FloatBuffer copyIntoNewBuffer(double[] data) {
		FloatBuffer out = BufferUtil.newFloatBuffer(data.length);
		for (double element : data) {
			out.put((float) element);
		}
		return out;
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param vertices
	 */
	public void setVertices(double[] verts) {
		if (verts.length % 3 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setVertices(): Vertex array length is not a multiple of three.");
		}
		this.verts = copyIntoNewBuffer(verts);
		this.numVertices = verts.length / 3;
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param normals
	 */
	public void setNormals(double[] normals) {
		if (normals.length % 3 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setNormals(): Normal array length is not a multiple of three.");
		}
		this.normals = copyIntoNewBuffer(normals);
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param texes
	 */
	public void setTexCoordinates(double[] texes) {
		if (texes.length % 2 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setTexCoordinates(): Texture coordinate array length is not a multiple of 2.");
		}
		this.texcoords = copyIntoNewBuffer(texes);
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param triangles
	 */
	public void setTriangles(int[] triangles) {
		if (triangles.length % 3 != 0) {
			throw new Error(
					"CS569.Objects.MeshObject.setTriangles(): Triangle array length is not a multiple of 3.");
		}
		this.numTriangles = triangles.length / 3;
		this.triangles = BufferUtil.newIntBuffer(triangles.length);
		this.triangles.put(triangles);
	}

	/**
	 * Converts the Vector into the appropriate array type
	 * 
	 * @return
	 */
	private static FloatBuffer convertVector3fList(Vector<Vector3f> data) {

		if (data.size() == 0) {
			return null;
		}
		FloatBuffer out = BufferUtil.newFloatBuffer(data.size() * 3);
		for (Vector3f v : data) {
			out.put(v.x);
			out.put(v.y);
			out.put(v.z);
		}
		return out;

	}

	/**
	 * Converts the Vector into the appropriate array type
	 * 
	 * @return
	 */
	private static FloatBuffer convertVector2fList(Vector<Vector2f> data) {

		if (data.size() == 0) {
			return null;
		}
		FloatBuffer out = BufferUtil.newFloatBuffer(data.size() * 2);
		for (Vector2f v : data) {
			out.put(v.x);
			out.put(v.y);
		}
		return out;

	}

	/**
	 * Converts the Vector into the appropriate array type
	 * 
	 * @return
	 */
	private static IntBuffer convertIntArrayList(Vector<int[]> data) {
		if (data.size() == 0) {
			return null;
		}
		IntBuffer out = BufferUtil.newIntBuffer(data.size() * 3);
		for (int[] i : data) {
			out.put(i[0]);
			out.put(i[1]);
			out.put(i[2]);
		}
		return out;
	}

	/**
	 * Creates a MeshObject from Vectors of objects.
	 * 
	 * @param vertexList
	 * @param normalList
	 * @param texList
	 * @param indexList
	 * @return
	 */
	private static MeshObject createFromVectors(Vector<Vector3f> vertexList,
			Vector<Vector3f> normalList, Vector<Vector2f> texList,
			Vector<int[]> indexList) {

		MeshObject out = new MeshObject();
		out.numVertices = vertexList.size();
		out.numTriangles = indexList.size();
		out.verts = convertVector3fList(vertexList);
		out.normals = convertVector3fList(normalList);
		out.texcoords = convertVector2fList(texList);
		out.triangles = convertIntArrayList(indexList);

		if (out.verts == null) {
			throw new Error(
					"CS569.Objects.MeshObject.createFromVectors(): .obj file contains no vertices.");
		}
		if (out.normals == null) {
			throw new Error(
					"CS569.Objects.MeshObject.createFromVectors(): .obj file contains no normals.");
		}
		if (out.triangles == null) {
			throw new Error(
					"CS569.Objects.MeshObject.createFromVectors(): .obj file contains no triangles.");
		}

		if (out.triangles.capacity() != 3 * out.numTriangles
				|| out.verts.capacity() != 3 * out.numVertices
				|| out.normals.capacity() != 3 * out.numVertices
				|| (out.texcoords != null && out.texcoords.capacity() != 2 * out.numVertices)) {
			throw new Error(
					"CS468.Objects.MeshObject.createFromVectors(): Mesh data is corrupted.  Incorrect number of elements.");
		}

		return out;

	}

	/**
	 * loads an object from an OBJ file This gives a single flat object with no
	 * children
	 */
	public static MeshObject loadFromOBJ(String filename, boolean normalize)
			throws OBJLoaderException, IOException {
		OBJLoader objloader = new OBJLoader(filename, normalize);
		MeshObject out = createFromVectors(objloader.getVertexList(), objloader
				.getNormalList(), objloader.getTexcoordList(), objloader
				.getIndexList());
		out.name = objloader.getObjectName();
		return out;
	}

	/**
	 * loads an object with groups from an OBJ file This creates an object with
	 * children: note that the children themselves will have no children of
	 * their own
	 */
	public static HierarchicalObject loadFromOBJwithGroups(String filename,
			boolean normalize) throws OBJLoaderException, IOException {
		OBJLoader objloader = new OBJLoader(filename, normalize);
		Vector<GroupData> groupList = objloader.getGroupList();

		// Create a scene to hold all the groups
		Scene scene = new Scene();
		scene.name = objloader.getObjectName();

		// Create the group nodes
		for (GroupData g : groupList) {
			MeshObject out = createFromVectors(g.vertexList, g.normalList,
					g.texcoordList, g.indexList);
			out.name = g.name;
			scene.addObject(out);
		}

		return scene;
	}

	/**
	 * @see cs569.object.HierarchicalObject#writeLocalData(java.io.PrintStream,
	 *      int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		WritingUtils.writeFloatBuffer(out, verts, "vertices", indent);
		WritingUtils.writeFloatBuffer(out, normals, "normals", indent);
		if (texcoords != null) {
			WritingUtils.writeFloatBuffer(out, texcoords, "texCoordinates",
					indent);
		}
		WritingUtils.writeIntBuffer(out, triangles, "triangles", indent);
	}
	

	/**
	 * Helper function for calculateNormals() - returns
	 * a Vector3f from a float buffer
	 */
	private Vector3f getVector3(FloatBuffer buf, int pos) {
		return new Vector3f(
			buf.get(pos*3+0), buf.get(pos*3+1), buf.get(pos*3+2)
		);
	}
	
	/**
	 * Helper function for calculateNormals() - adds
	 * a vector to an existing vector inside a float buffer
	 */
	private void addVector3(FloatBuffer buf, int pos, Vector3f vec) {
		float x = buf.get(pos*3+0), y = buf.get(pos*3+1), z = buf.get(pos*3+2);
		buf.put(pos*3+0, vec.x+x); buf.put(pos*3+1, vec.y+y); buf.put(pos*3+2, vec.z+z);
	}
	
	/**
	 * Generate surface normals (if needed)
	 */
	void generateNormals() {
		if (normals != null)
			return;
		normals = BufferUtil.newFloatBuffer(numVertices * 3);
		
		/* Reset to zero */
		for (int i=0; i<numVertices*3; i++)
			normals.put(0);
		

		for (int i=0; i<numTriangles; i++) {
			/* Retrieve all required triangle data */
			int index0 = triangles.get(i*3),
				index1 = triangles.get(i*3+1),
				index2 = triangles.get(i*3+2);
			Vector3f v0 = getVector3(verts, index0),
					 v1 = getVector3(verts, index1),
					 v2 = getVector3(verts, index2);

			Vector3f dP1 = new Vector3f(v1), dP2 = new Vector3f(v2);
			dP1.sub(v0); dP2.sub(v0);

			Vector3f normal = new Vector3f();
			normal.cross(dP1, dP2);
			normal.normalize();
			addVector3(normals, index0, normal);
			addVector3(normals, index1, normal);
			addVector3(normals, index2, normal);
		}
		
		for (int i=0; i<numVertices; i++) {
			Vector3f normal = getVector3(normals, i);
			normal.normalize();
			normals.put(i*3+0, normal.x);
			normals.put(i*3+1, normal.y);
			normals.put(i*3+2, normal.z);
		}
	}
}