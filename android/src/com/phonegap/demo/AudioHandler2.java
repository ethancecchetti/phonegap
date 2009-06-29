package com.phonegap.demo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.AssetFileDescriptor;
import android.media.*;
import android.util.Log;

public class AudioHandler2 {
	private Context mCtx;

	private HashMap<String, SongStatus> songInfo;
	private SoundPool player;

	private AssetManager assets;
	private AudioManager volumeControl;
	
	private static final int MUSIC_STREAM = AudioManager.STREAM_MUSIC;

	private class SongStatus {
//		public String file;
		public int fileID;
		public int streamID;
		public boolean isPaused;
		public boolean isPlaying;
		
		public SongStatus(int theFileID) {
//			file = theFile;
			fileID = theFileID;
			streamID = 0;
			isPaused = false;
			isPlaying = false;
		}
	}
	
	public AudioHandler2(String file, Context ctx, AssetManager assets) {
		this.mCtx = ctx;
		this.assets = assets;
		volumeControl = (AudioManager) mCtx.getSystemService(Context.AUDIO_SERVICE);
		songInfo = new HashMap<String, SongStatus>();
		player = new SoundPool(5, MUSIC_STREAM, 0);
	}

	protected void startPlaying(String file) {
		if ( !songInfo.containsKey(file) ) {
			AssetFileDescriptor fileAsset = getAssetFileDesc(file);

			int fileID = 0;
			if (fileAsset == null) {
				fileID = player.load(file, 1);
			}
			else {
//				fileID = player.load(fileAsset, 1);
			}

			SongStatus status = new SongStatus(fileID);
			songInfo.put(file, status);
			int streamID = player.play(fileID, 1, 1, 1, 0, 1);
			if (streamID != 0) {
				status.streamID = streamID;
				status.isPlaying = true;
			}
		}
		else if ( !songInfo.get(file).isPlaying ) {
			SongStatus status = songInfo.get(file);
			int streamID = player.play(status.fileID, 1, 1, 1, 0, 1);
			if (streamID != 0) {
				status.streamID = streamID;
				status.isPlaying = true;
				status.isPaused = false;
			}
		}
		else if ( songInfo.get(file).isPaused ) {
			player.resume(songInfo.get(file).fileID);
			songInfo.get(file).isPaused = false;
		}
		// otherwise do nothing
	}
	
	private AssetFileDescriptor getAssetFileDesc(String file) {
		if ( !file.startsWith("file:///android_asset/") ) {
			return null;
		}

		try {
			String filePath = file.substring(22);
			return assets.openFd(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void pausePlaying(String file) {
		if ( songInfo.containsKey(file) &&
		     songInfo.get(file).isPlaying &&
		     !songInfo.get(file).isPaused ) {
			player.pause( songInfo.get(file).streamID );
			songInfo.get(file).isPaused = true;
		}
	}

	protected void resumePlaying(String file) {
		if ( songInfo.containsKey(file) &&
		     songInfo.get(file).isPlaying &&
		     songInfo.get(file).isPaused ) {
			player.resume( songInfo.get(file).streamID );
			songInfo.get(file).isPaused = false;
		}
	}

	protected void stopPlaying(String file) {
		if ( songInfo.containsKey(file) && songInfo.get(file).isPlaying ) {
			SongStatus status = songInfo.get(file);

			player.stop( status.streamID );
			status.streamID = 0;
			status.isPlaying = false;
			status.isPaused = false;
		}
	}

	protected void stopAllPlaying() {
		for ( String file : songInfo.keySet() ) {
			stopPlaying(file);
		}
	}
	
	public void increaseVolume(int flags) {
		volumeControl.adjustStreamVolume(MUSIC_STREAM,
		                                 AudioManager.ADJUST_RAISE,
		                                 flags);
	}

	public void decreaseVolume(int flags) {
		volumeControl.adjustStreamVolume(MUSIC_STREAM,
		                                 AudioManager.ADJUST_LOWER,
		                                 flags);
	}

	public boolean setVolume(int percent, int flags) {
		if (percent < 0 || percent > 100)
			return false;

		int volIndex = percent * volumeControl.getStreamMaxVolume(MUSIC_STREAM) / 100;
		volumeControl.setStreamVolume(MUSIC_STREAM, volIndex, flags);
		return true;
	}
}
