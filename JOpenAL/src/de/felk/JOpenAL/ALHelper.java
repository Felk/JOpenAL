package de.felk.JOpenAL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.*;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCdevice;
import org.lwjgl.openal.EFX10;
import org.lwjgl.openal.OpenALException;

public class ALHelper {

	public static int MONO_SOURCES = -1;
	public static int STEREO_SOURCES = -1;
	public static int FREQUENCY = -1;

	/** Starts the playback of the given source */
	public static void play(int sourceID) throws OpenALException {
		alSourcePlay(sourceID);
		checkALError();
	}

	/** Stops the playback of the given source */
	public static void stop(int sourceID) throws OpenALException {
		alSourceStop(sourceID);
		checkALError();
	}

	/** Pauses the playback of the given source */
	public static void pause(int sourceID) throws OpenALException {
		alSourcePause(sourceID);
		checkALError();
	}

	/** Creates a new source and returns the id */
	public static int genSource() throws OpenALException {
		int i = alGenSources();
		checkALError();
		return i;
	}

	/** Creates a new buffer and returns the id */
	public static int genBuffer() throws OpenALException {
		int i = alGenBuffers();
		checkALError();
		return i;
	}

	/** Creates num new sources and returns an array of ids */
	public static int[] genSources(int num) throws OpenALException {
		IntBuffer ib = BufferUtils.createIntBuffer(num);
		alGenSources(ib);
		checkALError();
		return ib.array();
	}

	/** Creates num new buffers and returns an array of ids */
	public static int[] genBuffers(int num) throws OpenALException {
		IntBuffer ib = BufferUtils.createIntBuffer(num);
		alGenBuffers(ib);
		checkALError();
		return ib.array();
	}

	/**
	 * Sets all the data for a buffer
	 * 
	 * @param bufferID
	 *            id of the buffer to load everything into
	 * @param format
	 *            AL format of the data
	 * @param data
	 *            PCM audio data
	 * @param samplerate
	 * @throws OpenALException
	 */
	public static void setBuffer(int bufferID, int format, ByteBuffer data, int samplerate) throws OpenALException {
		alBufferData(bufferID, format, data, samplerate);
		checkALError();
	}

	/** makes the given source play back the audio data from the given buffer */
	public static void bindBufferToSource(int bufferID, int sourceID) throws OpenALException {
		alSourcei(sourceID, AL_BUFFER, bufferID);
		checkALError();
	}

	/** returns the raw size of a buffer in bytes */
	public static int getBufferSize(int id) {
		int result = AL10.alGetBufferi(id, AL_SIZE);
		checkALError();
		return result;
	}

	public static int getBufferChannels(int id) {
		int result = AL10.alGetBufferi(id, AL_CHANNELS);
		checkALError();
		return result;
	}

	public static void setPosition(int sourceID, Vector position) throws OpenALException {
		alSource3f(sourceID, AL_POSITION, position.getX(), position.getY(), 0);
		checkALError();
	}

	public static void setVelocity(int sourceID, Vector velocity) throws OpenALException {
		alSource3f(sourceID, AL_VELOCITY, velocity.getX(), velocity.getY(), 0);
		checkALError();
	}

	public static void setLooping(int sourceID, boolean looping) throws OpenALException {
		if (looping)
			alSourcei(sourceID, AL_LOOPING, AL_TRUE);
		else
			alSourcei(sourceID, AL_LOOPING, AL_FALSE);
		checkALError();
	}

	public static void setPitch(int sourceID, float pitch) throws OpenALException {
		alSourcef(sourceID, AL_PITCH, pitch);
		checkALError();
	}

	public static void setGain(int sourceID, float gain) throws OpenALException {
		alSourcef(sourceID, AL_GAIN, gain);
		checkALError();
	}

	public static void setOffset(int sourceID, int bufferSize, float offset) throws OpenALException {
		alSourcei(sourceID, AL_BYTE_OFFSET, (int) (offset * bufferSize));
		checkALError();
	}

	public static int getByteOffset(int sourceID) throws OpenALException {
		int result = alGetSourcei(sourceID, AL_BYTE_OFFSET);
		checkALError();
		return result;
	}

	/** Retrieves the state of the given source. Can be something like initial, playing, paused, stop etc. */
	public static int getSourceState(int sourceID) throws OpenALException {
		int result = alGetSourcei(sourceID, AL_SOURCE_STATE);
		checkALError();
		return result;
	}

	/** Sets the global rolloff factor. (Lowering of volume over distance) */
	public static void setRolloffFactor(int sourceID, float factor) {
		alSourcef(sourceID, AL_ROLLOFF_FACTOR, factor);
	}

	/**
	 * To activate an additional extension, you need to check for its presence. This returns whether .ogg vorbis is supported or not. .ogg files are converted to PCM data via JOrbis anyway, because the vorbis extension is not supported anymore
	 */
	public static boolean initVorbisExtension() {
		if (alIsExtensionPresent("AL_EXT_vorbis")) {
			checkALError();
			return true;
		} else {
			checkALError();
			return false;
		}
	}

	/**
	 * Sets the properties for the listener.
	 * 
	 * @param position
	 *            float[]{x, y, z} representing the listener's position
	 * @param velocity
	 *            float[]{x, y, z} representing the listener's velocity
	 * @param orientationAt
	 *            float[]{x, y, z} representing the normalized listener's facing direction
	 * @param orientationUp
	 *            float[]{x, y, z} representing the normalized listener's upside direction
	 */
	public static void setListener(float[] position, float[] velocity, float[] orientationAt, float[] orientationUp) {
		alListener3f(AL_POSITION, position[0], position[1], position[2]);
		checkALError();
		alListener3f(AL_VELOCITY, velocity[0], velocity[1], velocity[2]);
		checkALError();
		FloatBuffer ori = BufferUtils.createFloatBuffer(6);
		ori.put(0, orientationAt[0]);
		ori.put(1, orientationAt[1]);
		ori.put(2, orientationAt[2]);
		ori.put(3, orientationUp[0]);
		ori.put(4, orientationUp[1]);
		ori.put(5, orientationUp[2]);
		alListener(AL_ORIENTATION, ori);
		//alListenerf(AL_REFERENCE_DISTANCE, 0f);
		checkALError();
	}

	/** Frees the given source */
	public static void destroySource(int sourceID) throws OpenALException {
		alDeleteSources(sourceID);
		checkALError();
	}

	/** Frees the given buffer (frees the audio data) */
	public static void destroyBuffer(int bufferID) throws OpenALException {
		alDeleteBuffers(bufferID);
		checkALError();
	}

	/** Frees the given sources */
	public static void destroySources(int[] sourceIDs) throws OpenALException {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(sourceIDs.length);
		intBuffer.put(sourceIDs);
		alDeleteSources(intBuffer);
		checkALError();
	}

	/** Frees the given buffers (frees the audio data) */
	public static void destroyBuffers(int[] bufferIDs) throws OpenALException {
		IntBuffer intBuffer = BufferUtils.createIntBuffer(bufferIDs.length);
		intBuffer.put(bufferIDs);
		alDeleteBuffers(intBuffer);
		checkALError();
	}

	/** reads attributes from the current device and stores it in pseudo constants */
	public static void readDeviceAttributes() {

		ALCdevice device = AL.getDevice();

		IntBuffer buffer = BufferUtils.createIntBuffer(1);

		alcGetInteger(device, ALC_ATTRIBUTES_SIZE, buffer);
		checkALCError();

		int length = buffer.get(0);
		buffer = BufferUtils.createIntBuffer(length);

		alcGetInteger(device, ALC_ALL_ATTRIBUTES, buffer);
		checkALCError();

		for (int i = 0; i + 1 < buffer.limit(); i += 2) {
			if (buffer.get(i) == ALC_MONO_SOURCES) {
				ALHelper.MONO_SOURCES = buffer.get(i + 1);
				//System.out.println("ALC_MONO_SOURCES: " + buffer.get(i + 1));
			} else if (buffer.get(i) == ALC_STEREO_SOURCES) {
				ALHelper.STEREO_SOURCES = buffer.get(i + 1);
				//System.out.println("ALC_STEREO_SOURCES: " + buffer.get(i + 1));
			} else if (buffer.get(i) == ALC_FREQUENCY) {
				ALHelper.FREQUENCY = buffer.get(i + 1);
				//System.out.println("ALC_FREQUENCY: " + buffer.get(i + 1));
			} else if (buffer.get(i) == AL_BUFFER) {
				//System.out.println("AL_BUFFER: " + buffer.get(i + 1));
			} else if (buffer.get(i) == ALC_REFRESH) {
				//System.out.println("ALC_REFRESH: " + buffer.get(i + 1));
			} else if (buffer.get(i) == EFX10.ALC_MAX_AUXILIARY_SENDS) {
				//System.out.println("ALC_MAX_AUXILIARY_SENDS: " + buffer.get(i + 1));
			} else {
				//System.out.println("unspecified: " + buffer.get(i) + " > " + buffer.get(i + 1));
			}

		}
		//System.out.println("Buffer content: "+buffer.get(0));

	}

	/** returns a String representing the given AL error id */
	private static String getALErrorString(int err) {
		switch (err) {
		case AL_NO_ERROR:
			return "AL_NO_ERROR";
		case AL_INVALID_NAME:
			return "AL_INVALID_NAME";
		case AL_INVALID_ENUM:
			return "AL_INVALID_ENUM";
		case AL_INVALID_VALUE:
			return "AL_INVALID_VALUE";
		case AL_INVALID_OPERATION:
			return "AL_INVALID_OPERATION";
		case AL_OUT_OF_MEMORY:
			return "AL_OUT_OF_MEMORY";
		default:
			return "No such error code";
		}
	}

	/** returns a String representing the given ALC error id */
	private static String getALCErrorString(int err) {
		switch (err) {
		case ALC_NO_ERROR:
			return "AL_NO_ERROR";
		case ALC_INVALID_DEVICE:
			return "ALC_INVALID_DEVICE";
		case ALC_INVALID_CONTEXT:
			return "ALC_INVALID_CONTEXT";
		case ALC_INVALID_ENUM:
			return "ALC_INVALID_ENUM";
		case ALC_INVALID_VALUE:
			return "ALC_INVALID_VALUE";
		case ALC_OUT_OF_MEMORY:
			return "ALC_OUT_OF_MEMORY";
		default:
			return "no such error code";
		}
	}

	public static void checkALError() throws OpenALException {
		int error = alGetError();
		if (error != AL_NO_ERROR) {
			throw new OpenALException(getALErrorString(error));
		}
	}

	public static void checkALCError() throws OpenALException {
		int error = alGetError();
		if (error != ALC_NO_ERROR) {
			throw new OpenALException(getALCErrorString(error));
		}
	}

}
