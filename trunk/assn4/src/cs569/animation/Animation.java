package cs569.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * Keyframe-based animation consisting of a set of animation tracks
 *
 * Created on March 5, 2008
 * Course: CS569 (Interactive Computer Graphics) by Steve Marschner
 * Originally written for CS467/468 (Computer Graphics II and Practicum) by Kavita Bala
 * Copyright 2007 Computer Science Department, Cornell University
 * 
 * @author Wenzel Jakob
 */
public class Animation implements Animated {
	private List<AnimationTrack<?>> tracks;

	public Animation() {
		tracks = new ArrayList<AnimationTrack<?>>();
	}
	
	public void addTrack(AnimationTrack<?> track) {
		tracks.add(track);
	}

	public List<AnimationTrack<?>> getTracks() {
		return tracks;
	}

	public void update(float time) {
		for (AnimationTrack<?> track : tracks)
			track.update(time);
	}
}
