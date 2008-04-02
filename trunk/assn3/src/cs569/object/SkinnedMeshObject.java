	package cs569.object;

import java.io.PrintStream;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4f;

import com.sun.opengl.util.BufferUtil;

import cs569.misc.GLSLErrorException;
import cs569.misc.GLUtils;
import cs569.misc.WritingUtils;

/**
 * Extended mesh object with support for per-vertex weights&indices.
 * Takes care of passing this information to a vertex shader
 * which performs the actual skinning operations.
 * 
 * Created on January 26, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class SkinnedMeshObject extends TangentSpaceMeshObject {
	/** Multiple sets of skinning weights (This array has 'attributeCount' entries) */
	protected FloatBuffer[] weights;

	/** Associated bone indices (floating point values because GLSL wants
	    them that way. They need to be re-cast into integers within the
	    shader) */
	protected FloatBuffer[] weightIndices;

	/** Transformations for conversion between bone space and object space */
	protected Matrix4f[] offsetTransforms;

	/** Bone names */
	protected String[] boneNames;
	
	/** The number of bones that are used to skin this mesh */
	protected int boneCount;

	/** The number of additional attributes per vertex */
	protected int attributeCount;
		
	protected Matrix4f[] boneTransforms = null;
	
	// glsl location handles
	int weightHandle1, weightHandle2, weightHandle3;	
	int weightHandle4, weightHandle5, weightHandle6;	
	int b1, b2, b3, b4, b5, b6;
	
	
	/**
	 * Should be used only be the Parser. Please name your objects!
	 */
	public SkinnedMeshObject() {
		/* The skinned object is transformed by its skeleton
		 * and should not be moved by itself.
		 */
		setUpdateList(NO_UPDATE);
		
	}

	/**
	 * Create an empty skinned mesh object
	 */
	public SkinnedMeshObject(String inName) {
		super(inName);
		/* The skinned object is transformed by its skeleton
		 * and should not be moved by itself.
		 */
		setUpdateList(NO_UPDATE);
		
	}

	/**
	 * Verify that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly
	 */
	@Override
	protected boolean isConfiguredForShader(GL gl) {
		if (!super.isConfiguredForShader(gl))
			return false;

		if (getMaterial().needsSkin() == false)
		{
			return true;
		}
		
		if (boneTransforms == null)
			return false;
		else
			return true;
	}

	/**
	 * Ensure that the currently running shader is hooked up to
	 * this mesh's additional attributes/uniforms properly
	 */
	@Override
	protected void configureForShader(GL gl) {
		super.configureForShader(gl);
		
		if (getMaterial().needsSkin() == false)
		{
			return;
		}
		
		weightHandle1 = gl.glGetAttribLocation(program, "w1");
		weightHandle2 = gl.glGetAttribLocation(program, "w2");
		weightHandle3 = gl.glGetAttribLocation(program, "w3");
		weightHandle4 = gl.glGetAttribLocation(program, "w4");
		weightHandle5 = gl.glGetAttribLocation(program, "w5");
		weightHandle6 = gl.glGetAttribLocation(program, "w6");		
		if (weightHandle1 < 0 || weightHandle2 < 0 || weightHandle3 < 0 || weightHandle4 < 0 || weightHandle5 < 0 || weightHandle6 < 0)
		{
			System.out.println("Warning: weight attribute not found!");
			return; // return without configuring
		}


		b1 = gl.glGetAttribLocation(program, "bIndex1");
		b2 = gl.glGetAttribLocation(program, "bIndex2");
		b3 = gl.glGetAttribLocation(program, "bIndex3");
		b4 = gl.glGetAttribLocation(program, "bIndex4");
		b5 = gl.glGetAttribLocation(program, "bIndex5");
		b6 = gl.glGetAttribLocation(program, "bIndex6");		
		if (b1 < 0 || b2 < 0 || b3 < 0 || b4 < 0 || b5 < 0 || b6 < 0)
		{
			System.out.println("Warning: bone index attribute not found!");
			return; // return without configuring
		}
		


		
		boneTransforms = new Matrix4f[boneCount];
		for (int i=0; i<boneTransforms.length; i++)
		{
			boneTransforms[i] = new Matrix4f();
		}			
				
	}

	/**
	 * Pass on skinning data to the shader
	 * 
	 * Called every frame
	 */
	@Override
	protected void connectToShader(GL gl) {
		super.connectToShader(gl);
		
		if (getMaterial().needsSkin() == false)
		{
			return;
		}
		
		HierarchicalObject ho ;
		HierarchicalObject parent = (HierarchicalObject) this.getParent();
		for (int i=0; i<boneNames.length; i++)
		{
	      // the skeleton is grouped as a sibling to this object, so it is easiest
	      // to start at the parent
		  ho = parent.findByName(boneNames[i]);
		  if (ho == null)
		  {
			 System.out.println("Missing bone " + boneNames[i] + "!");
			 //TODO set failed/unconfigured
		  }
		  //System.out.println("bontras length=" + boneTransforms.length);
		  boneTransforms[i].set(ho.worldTransform);
		  boneTransforms[i].mul(boneTransforms[i],offsetTransforms[i]);
	
		}
 
		for (int i=0; i<boneCount; i++)        
		{
			int boneMatHandle = getNamedParameter(gl, "boneMatrix["+i+"]");
			gl.glUniformMatrix4fv(boneMatHandle, 1, false, GLUtils.fromMatrix4f(boneTransforms[i]), 0);	
		}			
		
		for (int i=0; i<6; i++)
		{
			weights[i].rewind();
			weightIndices[i].rewind();			
		}
				
		gl.glEnableVertexAttribArray(weightHandle1);
		gl.glEnableVertexAttribArray(weightHandle2);
		gl.glEnableVertexAttribArray(weightHandle3);
		gl.glEnableVertexAttribArray(weightHandle4);
		gl.glEnableVertexAttribArray(weightHandle5);
		gl.glEnableVertexAttribArray(weightHandle6);
		gl.glEnableVertexAttribArray(b1);
		gl.glEnableVertexAttribArray(b2);
		gl.glEnableVertexAttribArray(b3);
		gl.glEnableVertexAttribArray(b4);
		gl.glEnableVertexAttribArray(b5);
		gl.glEnableVertexAttribArray(b6);

		//glVertexAttrib1f(loc,2.0);		
		gl.glVertexAttribPointer(weightHandle1, 1, GL.GL_FLOAT, false, 0, weights[0]);
		gl.glVertexAttribPointer(weightHandle2, 1, GL.GL_FLOAT, false, 0, weights[1]);
		gl.glVertexAttribPointer(weightHandle3, 1, GL.GL_FLOAT, false, 0, weights[2]);
		gl.glVertexAttribPointer(weightHandle4, 1, GL.GL_FLOAT, false, 0, weights[3]);
		gl.glVertexAttribPointer(weightHandle5, 1, GL.GL_FLOAT, false, 0, weights[4]);
		gl.glVertexAttribPointer(weightHandle6, 1, GL.GL_FLOAT, false, 0, weights[5]);
		
				
		gl.glVertexAttribPointer(b1, 1, GL.GL_FLOAT, false, 0, weightIndices[0]);
		gl.glVertexAttribPointer(b2, 1, GL.GL_FLOAT, false, 0, weightIndices[1]);
		gl.glVertexAttribPointer(b3, 1, GL.GL_FLOAT, false, 0, weightIndices[2]);
		gl.glVertexAttribPointer(b4, 1, GL.GL_FLOAT, false, 0, weightIndices[3]);
		gl.glVertexAttribPointer(b5, 1, GL.GL_FLOAT, false, 0, weightIndices[4]);
		gl.glVertexAttribPointer(b6, 1, GL.GL_FLOAT, false, 0, weightIndices[5]);
		
		
					
	}
	
	protected int getNamedParameter(GL gl, String name) //throws GLSLErrorException 
	{
     int location = gl.glGetUniformLocation(program, name);
     if (location == -1)
   	   System.out.println("Warning: Parameter '" + name + "' not found!");
     return location;
    }

	/**
	 * Disconnect the mesh-related shader uniforms/attributes
	 */
	@Override
	protected void disconnectFromShader(GL gl) {
		super.disconnectFromShader(gl);
		
		if (getMaterial().needsSkin() == false)
		{
			return;
		}
		
		gl.glDisableVertexAttribArray(weightHandle1);
		gl.glDisableVertexAttribArray(weightHandle2);
		gl.glDisableVertexAttribArray(weightHandle3);
		gl.glDisableVertexAttribArray(weightHandle4);
		gl.glDisableVertexAttribArray(weightHandle5);
		gl.glDisableVertexAttribArray(weightHandle6);
		gl.glDisableVertexAttribArray(b1);
		gl.glDisableVertexAttribArray(b2);
		gl.glDisableVertexAttribArray(b3);
		gl.glDisableVertexAttribArray(b4);
		gl.glDisableVertexAttribArray(b5);
		gl.glDisableVertexAttribArray(b6);

	}
	
	/**
	 * Should only be used by the Parser.
	 * 
	 * @param weights
	 */
	public void setWeights(double[] weights) {
		if (weights.length % numVertices != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setWeights(): Weight array length is not a multiple of the vertex count. ("+weights.length+" vs " + numVertices + ")");
		}
		int setCount = weights.length / numVertices;
		this.weights = new FloatBuffer[setCount];
		for (int i=0; i<setCount; i++) {
			this.weights[i] = BufferUtil.newFloatBuffer(numVertices);
			for (int j=0; j<numVertices; j++) {
				this.weights[i].put((float) weights[j+i*numVertices]);				
			}
			//System.out.println("i="+i+", put = " + weights[i*numVertices]);
		}
		//System.out.println("setCount = " + setCount);
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param weightIndices
	 */
	public void setWeightIndices(int[] indices) {
		if (indices.length % numVertices != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setWeightIndices(): Weight index array length is not a multiple of the vertex count.");
		}
		int setCount = indices.length / numVertices;
		this.weightIndices = new FloatBuffer[setCount];
		for (int i=0; i<setCount; i++) {
			this.weightIndices[i] = BufferUtil.newFloatBuffer(numVertices);
			for (int j=0; j<numVertices; j++) {
				this.weightIndices[i].put(indices[j+i*numVertices]);
			}
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setOffsetTransformArray(double[] data) {
		if (data.length % 16 != 0) {
			throw new Error(
					"CS569.Skin.SkinnedMeshObject.setOffsetTransformArray(): Value array length is not a multiple of 16.");
		}
		int boneCount = data.length / 16;
		offsetTransforms = new Matrix4f[boneCount];
		for (int i=0; i<boneCount; i++) {
			offsetTransforms[i] = new Matrix4f();
			float[] temp = new float[16];
			for (int j = 0; j < 16; j++)
				temp[j] = (float) data[j+i*16];
			offsetTransforms[i].set(temp);
			offsetTransforms[i].transpose();
		}
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setAttributeCount(int attributeCount) {
		this.attributeCount = attributeCount;
		//System.out.println("attributeCount = " + attributeCount );
	}

	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setBoneCount(int boneCount) {
		this.boneCount = boneCount;
	}
	
	/**
	 * Should only be used by the Parser.
	 * 
	 * @param transforms
	 */
	public void setBoneNames(String[] boneNames) {
		this.boneNames = boneNames;
	}

	/**
	 * @see cs569.object.HierarchicalObject#writeLocalData(java.io.PrintStream,
	 *      int)
	 */
	@Override
	protected void writeLocalData(PrintStream out, int indent) {
		super.writeLocalData(out, indent);
		WritingUtils.writeInt(out, boneCount, "boneCount", indent);
		WritingUtils.writeInt(out, attributeCount, "attributeCount", indent);
		WritingUtils.writeStringArray(out, boneNames, "boneNames", indent);
		out.print(WritingUtils.getIndentString(indent)+"<weights>");
		for (int i=0; i<this.weights.length; i++) {
			FloatBuffer weights = (FloatBuffer) this.weights[i];
			weights.rewind();
			for (int j = 0; j < weights.capacity(); j++) {
				out.print(weights.get() + " ");
			}
		}
		out.println("</weights>");
		out.print(WritingUtils.getIndentString(indent)+"<weightIndices>");
		for (int i=0; i<this.weightIndices.length; i++) {
			FloatBuffer indices = (FloatBuffer) this.weightIndices[i];
			indices.rewind();
			for (int j = 0; j < indices.capacity(); j++) {
				out.print((int) indices.get() + " ");
			}
		}
		out.println("</weightIndices>");
		out.print(WritingUtils.getIndentString(indent)+"<offsetTransformArray>");
		for (int i=0; i<boneCount; i++) {
			Matrix4f trafo = new Matrix4f(offsetTransforms[i]);
			trafo.transpose();
			out.print(trafo.toString().replace('\n', ' ').replace(',', ' ')+" ");			
		}
		out.println("</offsetTransformArray>");
	}
}