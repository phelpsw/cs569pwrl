package cs569.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.vecmath.Point3i;
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
public class OBJLoader {
	protected Vector<Vector3f> vertexList = new Vector<Vector3f>();
	protected Vector<Vector3f> normalList = new Vector<Vector3f>();
	protected Vector<int[]> indexList = new Vector<int[]>();
	protected Vector<Vector2f> texcoordList = new Vector<Vector2f>();
	protected Vector<String> fileString = new Vector<String>();
	protected String objectName, materialName;
	protected Vector<GroupData> groupList = new Vector<GroupData>();

	public OBJLoader(String filename) throws IOException, OBJLoaderException {
		loadFile(filename, true);
	}

	public OBJLoader(String filename, boolean normalize) throws IOException,
			OBJLoaderException {
		loadFile(filename, normalize);
	}

	protected void loadFile(String filename, boolean normalize)
			throws IOException, OBJLoaderException {
		File f = new File(filename);
		BufferedReader reader;
		try {
			FileReader fr = new FileReader(f);
			reader = new BufferedReader(fr);
		} catch (IOException e) {
			throw new IOException(
					e
							+ "\nUnable to create FileReader: File may not be a text file");
		}

		// we save a copy of the entire file, as lines of strings
		String s;
		while ((s = reader.readLine()) != null) {
			fileString.add(s);
		}
		reader.close();

		// finally get data from the file
		parseFile();

		// do some extra computations
		if (normalList.size() == 0) {
			computeNormals();
		}

		// resize the object to fit a canonical cube if specified
		if (normalize) {
			normalize();
		}

		// expand the groups out
		expandGroups();

		// make sure the representation is OK
		repOK();

		// Just to catch invalid files, we'll throw an Exception if no vertices
		// are
		// found
		if (vertexList.size() <= 0) {
			throw new OBJLoaderException(
					"Error: No vertices found.\nFile may not be a valid .OBJ file.");
		}
	}

	protected void parseFile() throws OBJLoaderException {
		// we'll parse the file in a series of passes.
		// This way we can be sure that we have info we need when we need them

		Vector<Vector3f> vlist = new Vector<Vector3f>();
		Vector<Vector3f> nlist = new Vector<Vector3f>();
		Vector<Vector2f> tlist = new Vector<Vector2f>();

		// first pass: vertex positions
		for (String currToken : fileString) {
			StringTokenizer st = new StringTokenizer(currToken);
			while (st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if (firstToken.equals("v")) {
					// do some rudimentary error-checking for number of params
					if (st.countTokens() != 3) {
						throw new OBJLoaderException(
								"Invalid number of params for v: "
										+ st.countTokens());
					}

					double x, y, z;
					x = Double.parseDouble(st.nextToken());
					y = Double.parseDouble(st.nextToken());
					z = Double.parseDouble(st.nextToken());

					Vector3f v = new Vector3f((float) x, (float) y, (float) z);
					vlist.add(v);
				}
			}
		}

		// second pass: vertex normals
		for (String currToken : fileString) {
			StringTokenizer st = new StringTokenizer(currToken);
			while (st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if (firstToken.equals("vn")) {
					// do some rudimentary error-checking for number of params
					if (st.countTokens() != 3) {
						throw new OBJLoaderException(
								"Invalid number of params for vn: "
										+ st.countTokens());
					}

					double x, y, z;
					x = Double.parseDouble(st.nextToken());
					y = Double.parseDouble(st.nextToken());
					z = Double.parseDouble(st.nextToken());

					Vector3f v = new Vector3f((float) x, (float) y, (float) z);
					v.normalize();
					nlist.add(v);
				}
			}
		}

		// third pass: texture coordinates
		for (String currToken : fileString) {
			StringTokenizer st = new StringTokenizer(currToken);
			while (st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if (firstToken.equals("vt")) {
					// do some rudimentary error-checking for number of params

					double x, y;
					x = Double.parseDouble(st.nextToken());
					y = Double.parseDouble(st.nextToken());

					Vector2f v = new Vector2f((float) x, (float) y);
					tlist.add(v);
				}
			}
		}

		// fourth pass: face indices
		// This is slightly trickier, because there are several different
		// formats
		// for representing the faces

		// first we create a map for all possible triplets of (v, vt, vn)
		// VertexIndexMap indexMap = new VertexIndexMap( vlist.size(),
		// tlist.size(),
		// nlist.size());
		HashMap<Point3i, Integer> indexMap = new HashMap<Point3i, Integer>();

		String groupname = "Default";
		String material = "";
		// find the object name and sets the material to the first material
		// found in
		// the file
		for (String currToken : fileString) {
			StringTokenizer st = new StringTokenizer(currToken);
			boolean exit = false;
			while (st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if (firstToken.equals("o")) {
					try {
						groupname = st.nextToken();
					} catch (Exception e) {
						// do nothing
					}
					exit = true;
					break;
				} else if (firstToken.equals("usemtl") && material.equals("")) {
					try {
						material = st.nextToken();
					} catch (Exception e) {
						// do nothing
					}
				}
			}
			if (exit) {
				break;
			}
		}
		objectName = groupname;
		materialName = material;

		// next iterate through the file
		GroupData group = new GroupData();
		group.name = groupname;
		group.material = material;
		groupList.clear();
		for (String currToken : fileString) {
			StringTokenizer st = new StringTokenizer(currToken);
			while (st.hasMoreTokens()) {
				String firstToken = st.nextToken();
				if (firstToken.equals("g")) {
					try {
						groupname = st.nextToken();
					} catch (Exception e) {
						groupname = "";
					}

					if (groupList.size() == 0 && group.indexList.size() == 0) {
						// this is the very first group, and there was no
						// geometry prior to
						// this
						groupname = objectName;
					} else {
						groupList.add(group);
					}
					group = new GroupData();
					group.name = groupname;
					group.material = material;
				} else if (firstToken.equals("usemtl")) {
					try {
						material = st.nextToken();
					} catch (Exception e) {
						material = "";
					}
					group.material = material;
				} else if (firstToken.equals("f")) {
					// multi-sided polygon
					// We treat all n-sided polygons as convex, for simplicity
					int n = st.countTokens();
					int[] indices = new int[n];
					for (int i = 0; i < n; ++i) {
						int[] is = getIndices(st.nextToken());

						Point3i key = new Point3i(is[0], is[1], is[2]);
						if (!indexMap.containsKey(key)) {
							// need to create a new vertex
							vertexList.add(new Vector3f(vlist
									.elementAt(is[0] - 1)));
							if (tlist.size() > 0) {
								texcoordList.add(new Vector2f(tlist
										.elementAt(is[1] - 1)));
							}
							if (nlist.size() > 0) {
								normalList.add(new Vector3f(nlist
										.elementAt(is[2] - 1)));
							}

							int imapIndex = vertexList.size() - 1;

							indexMap.put(key, imapIndex);
							indices[i] = imapIndex;
						} else {
							indices[i] = indexMap.get(key);
						}
					}

					for (int i = 0; i < n - 2; ++i) {
						int[] array = new int[3];
						array[0] = indices[0];
						array[1] = indices[i + 1];
						array[2] = indices[i + 2];

						indexList.add(array);

						int[] array2 = new int[3];
						for (int j = 0; j < 3; ++j) {
							array2[j] = array[j];
						}
						group.indexList.add(array2);
					}
				}
			}
		}
		groupList.add(group);
	}

	protected void expandGroups() {
		// now we try to expand out the group into objects
		for (GroupData g : groupList) {
			g.vertexList.clear();
			g.normalList.clear();
			g.texcoordList.clear();

			int[] map = new int[vertexList.size()];
			for (int i = 0; i < vertexList.size(); ++i) {
				map[i] = -1;
			}

			for (int[] idx : g.indexList) {
				for (int i = 0; i < idx.length; ++i) {
					if (map[idx[i]] == -1) {
						g.vertexList.add(new Vector3f(vertexList
								.elementAt(idx[i])));
						g.normalList.add(new Vector3f(normalList
								.elementAt(idx[i])));
						if (texcoordList.size() > 0) {
							g.texcoordList.add(new Vector2f(texcoordList
									.elementAt(idx[i])));
						}

						map[idx[i]] = g.vertexList.size() - 1;
						idx[i] = map[idx[i]];
					} else {
						idx[i] = map[idx[i]];
					}
				}
			}
		}
	}

	protected int[] getIndices(String s) throws OBJLoaderException {
		int[] out = new int[3];

		// these will store the partitioned strings
		String[] st = new String[3];
		for (int i = 0; i < 3; ++i) {
			st[i] = "";
		}

		int currString = 0;
		for (int i = 0; i < s.length(); ++i) {
			// first do some error-checking
			// note that this also eliminates negative indices
			if (!((s.charAt(i) >= '0' && s.charAt(i) <= '9') || s.charAt(i) == '/')) {
				throw new OBJLoaderException(
						"Error: Unknown face index format: " + s);
			}

			if (s.charAt(i) != '/') {
				st[currString] += s.charAt(i);
			} else {
				if (currString < 2) {
					++currString;
				} else {
					throw new OBJLoaderException(
							"Error: Unknown face index format: " + s);
				}
			}
		}

		// now we can parse the tokens into integers
		for (int i = 0; i < 3; ++i) {
			if (st[i].length() == 0) {
				if (i == 0) {
					throw new OBJLoaderException(
							"Error: Unknown face index format: " + s);
				}
				out[i] = out[0];
			} else {
				out[i] = Integer.parseInt(st[i]);

				// the value 0 should never be used as an index in an OBJ file
				if (out[i] == 0) {
					throw new OBJLoaderException(
							"Error: Unknown face index format: " + s);
				}
			}
		}

		return out;
	}

	// for some files that do not have normals, compute them
	protected void computeNormals() {
		Vector3f[] faceNormal = new Vector3f[indexList.size()];

		// first compute the face-normals of each triangle
		Vector3f temp1 = new Vector3f();
		Vector3f temp2 = new Vector3f();
		int counter = 0;
		for (int[] idx : indexList) {
			faceNormal[counter] = new Vector3f();
			Vector3f v0 = vertexList.elementAt(idx[0]);
			Vector3f v1 = vertexList.elementAt(idx[1]);
			Vector3f v2 = vertexList.elementAt(idx[2]);
			temp1.sub(v1, v0);
			temp2.sub(v2, v0);
			faceNormal[counter].cross(temp1, temp2);
			faceNormal[counter].normalize();

			++counter;
		}

		// create the normal list
		for (int i = 0; i < vertexList.size(); ++i) {
			normalList.add(new Vector3f());
		}

		// sum up the normals at the vertices
		counter = 0;
		for (int[] idx : indexList) {
			for (int element : idx) {
				Vector3f n = normalList.elementAt(element);
				n.add(faceNormal[counter]);
			}
			++counter;
		}

		// average them all
		for (Vector3f n : normalList) {
			n.normalize();
		}
	}

	// checks the representation of the object
	public void repOK() throws OBJLoaderException {
		if (normalList.size() != vertexList.size()) {
			throw new OBJLoaderException("Error: Number of Vertices ("
					+ vertexList.size() + ") != Number of Normals ("
					+ normalList.size() + ").");
		}

		int counter = 0;
		for (int[] array : indexList) {

			// check for index out-of-bounds
			int max = vertexList.size() - 1;
			if (array[0] < 0 || array[1] < 0 || array[2] < 0 || array[0] > max
					|| array[1] > max || array[2] > max) {
				throw new OBJLoaderException(
						"Error: Index out of bounds: Idx: " + counter + " "
								+ array[0] + ", " + array[1] + ", " + array[2]
								+ " Max: " + max);
			}

			// check for degenerate triangles
			if (array[0] == array[1] || array[0] == array[2]
					|| array[1] == array[2]) {
				throw new OBJLoaderException(
						"Error: Degenerate triangles: Idx: " + counter + " "
								+ array[0] + ", " + array[1] + ", " + array[2]);
			}
			++counter;
		}
	}

	// for testing purposes, when dealing with potentially arbitrary sized
	// objects
	public void normalize() {
		double max = 0.0;

		for (Vector3f v : vertexList) {
			if (Math.abs(v.x) > max) {
				max = Math.abs(v.x);
			}
			if (Math.abs(v.y) > max) {
				max = Math.abs(v.y);
			}
			if (Math.abs(v.z) > max) {
				max = Math.abs(v.z);
			}
		}
		for (Vector3f v : vertexList) {
			v.x /= max;
			v.y /= max;
			v.z /= max;
		}
	}

	// accessor methods
	public Vector<Vector3f> getVertexList() {
		return vertexList;
	}

	public Vector<Vector3f> getNormalList() {
		return normalList;
	}

	public Vector<Vector2f> getTexcoordList() {
		return texcoordList;
	}

	public Vector<int[]> getIndexList() {
		return indexList;
	}

	public int getNumberOfVertices() {
		return vertexList.size();
	}

	public int getNumberOfIndices() {
		return indexList.size();
	}

	public Vector<GroupData> getGroupList() {
		return groupList;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getMaterialName() {
		return materialName;
	}

}