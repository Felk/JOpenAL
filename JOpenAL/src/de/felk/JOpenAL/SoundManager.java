package de.felk.JOpenAL;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.OpenALException;

public class SoundManager {

	private static ALSource[] sources;
	// Fill this with filenames and call preLoadSounds() somewhere at the start if you want to load sounds before first use
	private static ArrayList<String> preLoadedFiles = new ArrayList<String>();

	private static Vector lastListenerPosition;
	private static float lastUpdateTime = System.nanoTime() / 1000000000;
	public static final int LISTENER_HEIGHT = 2; // since this game is 2D, the listener is considered hovering LISTENER_HEIGHT meters above the world
	public static final float DEFAULT_ROLLOFF_FACTOR = 0.2f; // how fast sounds get silent with increasing distance (0 = always same volume)
	public static boolean alCreated = false; // just a flag determining if the AL context has already been created

	static {
		// Create OpenAL context, if not already done
		// Other classes MIGHT have static sound objects
		// and since they read in a buffer, they cause a early AL context creation 
		SoundManager.createAL();

		// Checks if the local implementation of OpenAL supports direct .ogg input
		// This sound API converts ogg vorbis files to PCM data via JOrbis anyway,
		// because OpenAL dropped the support on this
		//System.out.println(".ogg sound extension available: " + ALHelper.initVorbisExtension());

		// Gets some attributes from the current sound device
		ALHelper.readDeviceAttributes();

		// Create ALSource objects for all channels the sound device supports.
		// Each ALSource object is linked to a source created on the sound device
		sources = new ALSource[ALHelper.MONO_SOURCES];
		for (int i = 0; i < sources.length; i++) {
			sources[i] = new ALSource();
		}
	}

	/** creates the OpenAL Context, if it isn't already created */
	public static void createAL() {
		if (SoundManager.alCreated) return;
		try {
			AL.create(); // quick and easy way to initialize OpenAL with the default audio device
			alCreated = true;
		} catch (LWJGLException e) {
			System.out.println("Could not create OpenAL (Sound) Context!");
			e.printStackTrace();
		}
	}

	public static void addPreLoadSounds(String... sound) {
		for (String s : sound) {
			preLoadedFiles.add(s);
		}
	}
	
	public static void clearPreLoadSounds() {
		preLoadedFiles.clear();
	}

	/** Reads in all files marked for preloading. Call this at a point you want your programm to preload files */
	public static void preLoadSounds() {
		for (String filename : preLoadedFiles) {
			try {
				ALBufferBank.addSound(filename);
			} catch (OpenALException | IOException | LWJGLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets the OpenAL Listener
	 * @param position Position of the listener
	 * @param velocity Velocity of the listener
	 * @param orientationAt direction, the listener is looking at
	 * @param orientationUp direction, the listener's upside is pointing at
	 */
	public static void setListener(float[] position, float[] velocity, float[] orientationAt, float[] orientationUp) {
		ALHelper.setListener(position, velocity, orientationAt, orientationUp);
	}

	public static void update() {
		for (int i = 0; i < sources.length; i++) {
			if (sources[i] != null) {
				sources[i].update();
			}
		}
	}

	/** Frees all sources on the sound device */
	private static void clear() {
		for (int i = 0; i < sources.length; i++) {
			if (sources[i] != null) {
				sources[i].destroy();
				sources[i] = null;
			}
		}
	}

	/**
	 * This function is the lazy alternative to setListener. It assumes the listener's "eyes" are looking towards z+ with the "head" pointing towards y-. It also calculates the listener's velocity on its own by delta position
	 */
	public static void recalculateListener(Vector position) {

		float time = (float) System.nanoTime() / 1000000000;
		float elapsedTime = time - lastUpdateTime;
		if (elapsedTime > 0) {
			Vector velocity;
			if (lastListenerPosition == null) {
				velocity = position.multiply(1 / elapsedTime);
			} else {
				velocity = position.subtracted(lastListenerPosition).multiply(1 / elapsedTime);
			}
			lastUpdateTime = time;
			lastListenerPosition = position.clone();
			SoundManager.setListener(new float[] { position.getX(), position.getY(), LISTENER_HEIGHT }, new float[] { velocity.getX(), velocity.getY(), 0 }, new float[] { 0, 0, 1 }, new float[] { 0, -1, 0 });
		}

	}

	/**
	 * Returns an available source to play a sound back. If no free source or source with finished playback was found, it kicks out sounds with lower or equal priorities
	 * @param priority Priority of the new sound
	 * @return ALSource the sound can be played through, or null if no source is available
	 */
	static ALSource getFreeSource(int priority) {
		ALSource newSource = null;
		for (ALSource source : sources) {
			if (source.isUncoupled()) {
				return source;
			} else if (source.isStopped()) {
				source.uncouple();
				return source;
			} else if (source.priority <= priority) {
				priority = source.priority;
				newSource = source;
			}
		}
		if (newSource == null) {
			System.out.println("OpenAL Problem: All Sources are occupied. No free source for Priority " + priority + " was found!");
			return null;
		}
		// If the code reached here, a source with lower or equal priority (the lowest available) is considered the new source
		newSource.uncouple();
		return newSource;
	}

	/** Clears everything and shuts down the OpenAL Context */
	public static void shutdown() {
		ALBufferBank.clear();
		clear();
		AL.destroy();
	}

	static void updateGain(SoundCategory category) {
		for (int i = 0; i < sources.length; i++) {
			if (sources[i] != null) {
				sources[i].updateGain(category);
			}
		}
	}

}
