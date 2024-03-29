package cs569.object;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
/**
 * Created on January 26, 2007
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Adam Arbree -- arbree@cs.cornell.edu
 */
public class PrimitiveFactory {
	/**
	 * Creates a plane lying on the xz-plane 2 units in width and length. The
	 * plane's normal is the positive y-axis.
	 * 
	 * @return
	 */
	public static final MeshObject makePlane(String name) {
		return new TangentSpaceMeshObject(planeVerts, planeTriangles, planeNormals,
				planeTexCoords, name);
	}

	public static final MeshObject makePlane(String name, int numU, int numV) {
		
		float lengthU, lengthV;
		int numPlanes=numU*numV;
		
		lengthU = 1.0f/numU;
		lengthV = 1.0f/numV;
		// length of UV is 1 (0 to 1), length of plane is 2 (-1 to 1)
		
		float[] verts = new float[numPlanes*4 * 3];
		float[] norms = new float[numPlanes*4 * 3];
		float[] txt = new float[numPlanes*4 * 2];
		int[] tris = new int[numPlanes*2*3];
		
		Vector3f[] pos = new Vector3f[] {new Vector3f(),new Vector3f(),new Vector3f(),new Vector3f()};
		
		int xIndex=0;
		int zIndex=0;
		int base;
				
		for (int i=0; i<numPlanes; i++)
		{
			// go left to right, top to bottom
			xIndex = i%numU;
			zIndex = (int)i/numU;
			
			//System.out.println("plane #" + i + ", xIndex=" + xIndex+ ", zIndex =" +zIndex + " lengths:" + lengthU + ", " + lengthV);
		
		 //position starts in upper left, and goes counter-clockwise
		 pos[0].set(-1 + xIndex*lengthU*2, 0, -1 + zIndex*lengthV*2);		
		 pos[1].set(-1 + xIndex*lengthU*2, 0, -1 + (zIndex+1)*lengthV*2);
		
		 pos[2].set(-1 + (xIndex+1)*lengthU*2, 0, -1 + (zIndex+1)*lengthV*2);
		 pos[3].set(-1 + (xIndex+1)*lengthU*2, 0, -1 + zIndex*lengthV*2);
		 
		 // for each corner of the plane 
		 for (int j=0; j<4; j++)
		 {
	      base = i*12 + j*3;
		  verts[base] = pos[j].x;
		  verts[base + 1] = pos[j].y;
		  verts[base + 2] = pos[j].z;
		  //System.out.println(" corner " + j + ": " + pos[j]);
		  
		  //norms 0, 1, 0 
		  norms[base] = 0;
		  norms[base+1] = 1;
		  norms[base+2] = 0;		  
		 }
				
		 //texture: (0,0), (0,1), (1,1), (1,0)
		 txt[i*8] = 0; txt[i*8+1] = 0;
		 txt[i*8+2] = 0; txt[i*8+3] = 1;
		 txt[i*8+4] = 1; txt[i*8+5] = 1;
		 txt[i*8+6] = 1; txt[i*8+7] = 0;
		 		 		 
		//tris 0, 1, 2, 0, 2, 3
		 tris[i*6] = i*4;
		 tris[i*6+1] = i*4 + 1;
		 tris[i*6+2] = i*4 + 2;
		 tris[i*6+3] = i*4;
		 tris[i*6+4] = i*4 + 2;
		 tris[i*6+5] = i*4 + 3;
		 
		}
		
		
		return new TangentSpaceMeshObject(verts, tris, norms,
				txt, name);
	}

	/**
	 * Creates the box [-1,-1] to [1,1]
	 */
	public static final MeshObject makeBox(String name) {
		// Create the mesh
		return new TangentSpaceMeshObject(boxVerts, boxTriangles, boxNormals, boxTexCoords,
				name);
	}

	/**
	 * REQUIRED IMPLMENTATION FOR ASSIGNMENT 1. Creates a cone. The cone is
	 * exactly, 2 units high and 1 unit in radius at the base. The cone is
	 * oriented along the y-axis. The number of slices is the number of
	 * subdivisions around the circuference of the cone (at the base) and stacks
	 * is the number of subdivisions along the height.
	 * 
	 * @param stacks
	 * @param slices
	 */
	public static final MeshObject makeCone(int stacks, int slices, String name) 
	{

		// Create all the vertex data
		int size = slices + 2 + 2*(slices+1);
		float[] vertices = new float[3 * size];
		float[] normals = new float[3 * size];
		float[] texCoords = new float[2 * size];

		// Create the pole vertices
		int pos = 0;
		vertices[3 * pos] = 0;
		vertices[3 * pos + 1] = 1;
		vertices[3 * pos + 2] = 0;
		normals[3 * pos] = 0;
		normals[3 * pos + 1] = 1;
		normals[3 * pos + 2] = 0;
		texCoords[2 * pos] = 0.5f;
		texCoords[2 * pos + 1] = 0.5f;
		pos++;
		vertices[3 * pos] = 0;
		vertices[3 * pos + 1] = -1;
		vertices[3 * pos + 2] = 0;
		normals[3 * pos] = 0;
		normals[3 * pos + 1] = -1;
		normals[3 * pos + 2] = 0;
		texCoords[2 * pos] = 0.5f;
		texCoords[2 * pos + 1] = 0.5f;
		pos++;

		// Create the bottom cap vertices must duplicate the edges to get sharp corner
		for (int ctr = 0; ctr < slices; ctr++) {
			vertices[3 * pos] = cos(ctr * 2 * Math.PI / slices);
			vertices[3 * pos + 1] = -1;
			vertices[3 * pos + 2] = sin(ctr * 2 * Math.PI / slices);
			normals[3 * pos] = 0;
			normals[3 * pos + 1] = -1;
			normals[3 * pos + 2] = 0;
			texCoords[2 * pos] = (cos(ctr * 2 * Math.PI / slices) + 1) / 2;
			texCoords[2 * pos + 1] = (sin(ctr * 2 * Math.PI / slices) + 1) / 2;
			pos++;
		}
		
		// Create the intermediate vertices (base)
		for (int ctr1 = 0; ctr1 <= slices; ctr1++) {
			
			// The cosine and sine of the zenith
			float cAcross1 = cos(ctr1 * 2 * Math.PI / slices);
			float sAcross1 = sin(ctr1 * 2 * Math.PI / slices);

			// Create the vertices
			vertices[3 * pos] = cAcross1;
			vertices[3 * pos + 1] = -1.0f;
			vertices[3 * pos + 2] = sAcross1;

			normals[3 * pos] = cAcross1 / (float)Math.sqrt(5);
			normals[3 * pos + 1] = 2 / (float)Math.sqrt(5);
			normals[3 * pos + 2] = sAcross1 / (float)Math.sqrt(5);
			
			texCoords[2 * pos] = ctr1 / (float) slices;
			texCoords[2 * pos + 1] = 0.0f;
			pos++;

		}

		// Create the intermediate vertices (point)
		for (int ctr1 = 0; ctr1 <= slices; ctr1++) {
			
			// The cosine and sine of the zenith
			float cAcross1 = cos(ctr1 * 2 * Math.PI / slices);
			float sAcross1 = sin(ctr1 * 2 * Math.PI / slices);

			// Create the vertices
			vertices[3 * pos] = 0.0f;
			vertices[3 * pos + 1] = 1.0f;
			vertices[3 * pos + 2] = 0.0f;

			normals[3 * pos] = cAcross1 / (float)Math.sqrt(Math.pow(cAcross1,2) + Math.pow(sAcross1,2) + 4.0);
			normals[3 * pos + 1] = 2.0f / (float)Math.sqrt(Math.pow(cAcross1,2) + Math.pow(sAcross1,2) + 4.0);
			normals[3 * pos + 2] = sAcross1 / (float)Math.sqrt(Math.pow(cAcross1,2) + Math.pow(sAcross1,2) + 4.0);
			
			texCoords[2 * pos] = ctr1 / (float) slices;
			texCoords[2 * pos + 1] = 1.0f;
			pos++;

		}

		// Create the triangles

		int tris = 0;
		size = 3*slices;
		int[] triangles = new int[3 * size];

		// Bottom
		int i;
		for (i = 0; i < slices-1; i++) {
			triangles[3 * tris] = 1;
			triangles[3 * tris + 1] = i + 2;
			triangles[3 * tris + 2] = i + 3;
			tris++;
		}
		triangles[3 * tris] = 1;
		triangles[3 * tris + 1] = i + 2;
		triangles[3 * tris + 2] = 2;
		tris++;

		
		int offset = slices + 2;
		for(i=0; i < slices; i++)
		{
			triangles[3 * tris] = i+offset;
			triangles[3 * tris + 1] = i + 2*offset;
			triangles[3 * tris + 2] = i + 2*offset - 1;
			tris++;
			triangles[3 * tris] = i+offset;
			triangles[3 * tris + 1] = i+offset + 1;
			triangles[3 * tris + 2] = i + 2*offset;
			tris++;
		}
		
		/*for(i = 0; i<3*size; i+=3)
		{
			System.out.println(triangles[i] + " " + triangles[i+1] + " " +triangles[i+2]);
			System.out.println(triangles[i] + ": "+vertices[3*triangles[i]] + " " + vertices[3*triangles[i]+1] + " " +  vertices[3*triangles[i]+2]);
			System.out.println(triangles[i+1] + ": "+vertices[3*triangles[i+1]] + " " + vertices[3*triangles[i+1]+1] + " " +  vertices[3*triangles[i+1]+2]);
			System.out.println(triangles[i+2] + ": "+vertices[3*triangles[i+2]] + " " + vertices[3*triangles[i+2]+1] + " " +  vertices[3*triangles[i+2]+2]);
		}*/
		
		
		return new TangentSpaceMeshObject(vertices, triangles, normals, texCoords, name);

	}

	/**
	 * Creates a cylinder. The cylinder is exactly, 2 units high and 1 unit in
	 * radius. The cylinder is oriented along the y-axis. The number of slices
	 * is the number of subdivisions around the circuference of the cylinder and
	 * stacks is the number of subdivisions along the height.
	 * 
	 * @param stacks
	 * @param slices
	 */
	public static final MeshObject makeCylinder(int stacks, int slices,
			String name) {

		// Create all the vertex data
		int size = 2 * (slices + 1) + 2 + (stacks + 1) * (slices + 1);
		float[] vertices = new float[3 * size];
		float[] normals = new float[3 * size];
		float[] texCoords = new float[2 * size];

		// Create the pole vertices
		int pos = 0;
		vertices[3 * pos] = 0;
		vertices[3 * pos + 1] = 1;
		vertices[3 * pos + 2] = 0;
		normals[3 * pos] = 0;
		normals[3 * pos + 1] = 1;
		normals[3 * pos + 2] = 0;
		texCoords[2 * pos] = 0.5f;
		texCoords[2 * pos + 1] = 0.5f;
		pos++;
		vertices[3 * pos] = 0;
		vertices[3 * pos + 1] = -1;
		vertices[3 * pos + 2] = 0;
		normals[3 * pos] = 0;
		normals[3 * pos + 1] = -1;
		normals[3 * pos + 2] = 0;
		texCoords[2 * pos] = 0.5f;
		texCoords[2 * pos + 1] = 0.5f;
		pos++;

		// Create the cap vertices must duplicate the edges to get sharp corner
		for (int ctr = 0; ctr <= slices; ctr++) {

			vertices[3 * pos] = cos(ctr * 2 * Math.PI / slices);
			vertices[3 * pos + 1] = 1;
			vertices[3 * pos + 2] = sin(ctr * 2 * Math.PI / slices);
			normals[3 * pos] = 0;
			normals[3 * pos + 1] = 1;
			normals[3 * pos + 2] = 0;
			texCoords[2 * pos] = (cos(ctr * 2 * Math.PI / slices) + 1) / 2;
			texCoords[2 * pos + 1] = (sin(ctr * 2 * Math.PI / slices) + 1) / 2;
			pos++;
		}
		for (int ctr = 0; ctr <= slices; ctr++) {
			vertices[3 * pos] = cos(ctr * 2 * Math.PI / slices);
			vertices[3 * pos + 1] = -1;
			vertices[3 * pos + 2] = sin(ctr * 2 * Math.PI / slices);
			normals[3 * pos] = 0;
			normals[3 * pos + 1] = -1;
			normals[3 * pos + 2] = 0;
			texCoords[2 * pos] = (cos(ctr * 2 * Math.PI / slices) + 1) / 2;
			texCoords[2 * pos + 1] = (sin(ctr * 2 * Math.PI / slices) + 1) / 2;
			pos++;
		}

		// Create the intermediate vertices
		for (int ctr1 = 0; ctr1 <= stacks; ctr1++) {

			// The height of the current row
			float h = 1 - 2.0f * ctr1 / stacks;

			for (int ctr2 = 0; ctr2 <= slices; ctr2++) {

				// The cosine and sine of the zenith
				float cAcross1 = cos(ctr2 * 2 * Math.PI / slices);
				float sAcross1 = sin(ctr2 * 2 * Math.PI / slices);

				// Create the vertices
				vertices[3 * pos] = cAcross1;
				vertices[3 * pos + 1] = h;
				vertices[3 * pos + 2] = sAcross1;

				normals[3 * pos] = cAcross1;
				normals[3 * pos + 1] = 0;
				normals[3 * pos + 2] = sAcross1;
				texCoords[2 * pos] = ctr2 / (float) slices;
				texCoords[2 * pos + 1] = ctr1 / (float) stacks;
				pos++;

			}
		}

		// Create the triangles

		// Top
		int tris = 0;
		int nextV;
		size = 2 * slices + 2 * stacks * slices;
		int[] triangles = new int[3 * size];
		for (int i = 0; i < slices; i++) {
			nextV = i + 1;
			triangles[3 * tris] = 0;
			triangles[3 * tris + 2] = i + 2;
			triangles[3 * tris + 1] = nextV + 2;
			tris++;
		}

		// Bottom
		int bRowOffset = 2 + slices + 1;
		for (int i = 0; i < slices; i++) {
			nextV = i + 1;
			triangles[3 * tris] = 1;
			triangles[3 * tris + 1] = i + bRowOffset;
			triangles[3 * tris + 2] = nextV + bRowOffset;
			tris++;
		}

		// Middle
		int topL, topR, botL, botR;
		for (int j = 0; j < stacks; j++) {
			for (int i = 0; i < slices; i++) {

				topL = (j + 2) * (slices + 1) + i + 2;
				topR = topL + 1;
				botL = (j + 3) * (slices + 1) + i + 2;
				botR = botL + 1;

				triangles[3 * tris] = topL;
				triangles[3 * tris + 1] = topR;
				triangles[3 * tris + 2] = botL;
				tris++;
				triangles[3 * tris] = botL;
				triangles[3 * tris + 1] = topR;
				triangles[3 * tris + 2] = botR;
				tris++;

			}
		}

		return new TangentSpaceMeshObject(vertices, triangles, normals, texCoords, name);

	}

	/**
	 * Makes a 1 unit radius sphere centered at the origin. The number of slices
	 * is the number of divisions along the equator and stacks is the number of
	 * divisions along any meridian.
	 * 
	 * @param stacks
	 * @param slices
	 * @return
	 */
	public static final MeshObject makeSphere(int stacks, int slices,
			String name) {
		// Create all the vertex data
		int size = (stacks + 1) * (slices + 1);

		// slices have 20 vertices each
		float[] vertices = new float[3 * size];
		float[] normals = new float[3 * size];
		float[] texCoords = new float[2 * size];

		// Create the intermediate vertices
		int pos = 0;
		for (int ctr1 = 0; ctr1 <= stacks; ctr1++) {
			// The cosine and sine of the azimuth
			float cHeight1 = cos(ctr1 * Math.PI / stacks);
			float sHeight1 = sin(ctr1 * Math.PI / stacks);

			// Must duplicate the vertices on the wrapped edge to get the
			// textures to come out right
			for (int ctr2 = 0; ctr2 <= slices; ctr2++) {

				// The cosine and sine of the zenith
				float cAcross1 = cos(ctr2 * 2 * Math.PI / slices);
				float sAcross1 = sin(ctr2 * 2 * Math.PI / slices);

				vertices[3 * pos] = cAcross1 * sHeight1;
				vertices[3 * pos + 1] = cHeight1;
				vertices[3 * pos + 2] = sAcross1 * sHeight1;

				float x = cAcross1 * sHeight1;
				float y = cHeight1;
				float z = sAcross1 * sHeight1;
				float l = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);

				normals[3 * pos] = x * l;
				normals[3 * pos + 1] = y * l;
				normals[3 * pos + 2] = z * l;
				texCoords[2 * pos] = 1.0f - (ctr2 / ((float) slices));
				texCoords[2 * pos + 1] = 1.0f - (ctr1 / ((float) stacks));
				pos++;

			}
		}

		// Middle
		int tris = 0;
		size = 2 * slices * stacks;
		int[] triangles = new int[3 * size];
		int topL, topR, botL, botR;
		for (int j = 0; j < stacks; j++) {
			for (int i = 0; i < slices; i++) {

				topL = j * (slices + 1) + i;
				if (i==slices-1)
					topR = topL - i;
				else
					topR = topL + 1;
				botL = ((j + 1) * (slices + 1) + i);
				if (i==slices-1)
					botR = botL - i;
				else
					botR = botL + 1;

				triangles[3 * tris] = topL;
				triangles[3 * tris + 1] = topR;
				triangles[3 * tris + 2] = botL;
				tris++;
				triangles[3 * tris] = botL;
				triangles[3 * tris + 1] = topR;
				triangles[3 * tris + 2] = botR;
				tris++;

			}
		}

		return new TangentSpaceMeshObject(vertices, triangles, normals, texCoords, name);

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Math functions
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Quick casting version of cosine
	 * 
	 * @param d
	 * @return
	 */
	protected static float cos(double d) {
		return (float) Math.cos(d);
	}

	/**
	 * Quick casting version of sine
	 * 
	 * @param d
	 * @return
	 */
	protected static float sin(double d) {
		return (float) Math.sin(d);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// Static data for the plane and box
	// //////////////////////////////////////////////////////////////////////////////////////////////////

	private static final float[] planeVerts = new float[] { -1.0f, 0.0f, -1.0f,
			-1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f };

	private static final float[] planeNormals = new float[] { 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };

	private static final float[] planeTexCoords = new float[] { 0.0f, 0.0f,
			0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };

	private static final int[] planeTriangles = new int[] { 0, 1, 2, 0, 2, 3 };

	private static final float[] boxVerts = new float[] { 1.0f, 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f };

	private static final float[] boxNormals = new float[] { 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f,
			1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
			-1.0f, 0.0f, 0.0f, -1.0f, 0.0f };

	private static final float[] boxTexCoords = new float[] { 1.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
			1.0f };

	private static final int[] boxTriangles = new int[] { 0, 1, 2, 2, 3, 0, 4,
			5, 6, 6, 7, 4, 8, 9, 10, 10, 11, 8, 12, 13, 14, 14, 15, 12, 16, 17,
			18, 18, 19, 16, 20, 21, 22, 22, 23, 20 };
}
