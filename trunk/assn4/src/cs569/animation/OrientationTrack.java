package cs569.animation;

import javax.vecmath.Quat4f;

/**
 * Created on March 5, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */

public class OrientationTrack extends AnimationTrack<Quat4f> {
	private Quat4f interpQuat = new Quat4f();

	public OrientationTrack() {
	}

	public Keyframe createKeyframe(float[] data) {
		Keyframe keyframe = new Keyframe();
		assert(data.length == 5);
		keyframe.time = data[0];
		keyframe.value = new Quat4f(data[1], data[2], data[3], data[4]);
		return keyframe;
	}

	public void update(float relPos, Quat4f kfA, Quat4f kfB) {
		interpQuat.set(kfA);
		interpQuat.interpolate(kfB, relPos);
		object.setRotation(interpQuat);
	}
}
