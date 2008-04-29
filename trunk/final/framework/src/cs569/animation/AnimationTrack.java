package cs569.animation;

import java.util.ArrayList;
import java.util.List;

import cs569.object.HierarchicalObject;

/**
 * Abstract keyframe-based animation class
 *
 * Created on March 5, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public abstract class AnimationTrack<T> implements Animated {
	protected List<Keyframe> keyframes;
	protected int keyframeCount;
	protected String objectName;
	protected HierarchicalObject object;
	protected float animationLength;

	/* Abstract keyframe class */
	protected class Keyframe {
		float time;
		T value;
	};

	/* Create an empty animation track */
	public AnimationTrack() {
		keyframes = new ArrayList<Keyframe>();
		keyframeCount = 0;
		animationLength = 0.0f;
	}
	
	/** Interpolate between to values and set the result to 'object' */
	protected abstract void update(float relPos, T kfA, T kfB);

	/** Create a new keyframe from float[] data */
	public abstract Keyframe createKeyframe(float[] data);

	public void update(float time) {
		/* Repeat the animation */
		time -= Math.floor(time / animationLength) * animationLength;
		int low = findFrame(time);
		Keyframe kfA = keyframes.get(low);
		Keyframe kfB = keyframes.get(low+1);
		float relPos = Math.min(0.0f, Math.max(1.0f, (time - kfA.time) / (kfB.time - kfA.time)));
		if (object != null)
			update(relPos, kfA.value, kfB.value);
	}


	public void addKeyframe(float[] data) {
		Keyframe keyframe = createKeyframe(data);
		animationLength = Math.max(animationLength, keyframe.time);
		keyframes.add(keyframe);
	}
	
	/**
	 * Binary search to find keyframes for linear
	 * interpolation
	 */
	public int findFrame(float time) {
		assert(keyframes.size() > 2);

		int low = 0, high = keyframes.size()-1;
		while (high - low > 1) {
			int middle = (low + high) / 2;
			if (keyframes.get(middle).time > time)
				high = middle;
			else
				low = middle;
		}
		if (low+1 == keyframes.size())
			return low-1;
		return low;
	}
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectName() {
		return objectName;
	}
	
	public void setObject(HierarchicalObject object) {
		this.object = object;
	}
}
