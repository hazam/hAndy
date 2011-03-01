package com.hazam.handy.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FilesystemCache {
	protected File root;
	private final Context appCtx;

	public static String printEnvDirs() {
		final StringBuilder toret = new StringBuilder();
		toret.append("*-------ENVS-------*\n");
		toret.append("Data: "+Environment.getDataDirectory()+"\n");
		toret.append("DownloadCache: "+Environment.getDownloadCacheDirectory()+"\n");
		toret.append("ExternalStorage: "+Environment.getExternalStorageDirectory()+"\n");
		toret.append("Root: "+Environment.getRootDirectory()+"\n");
		toret.append("*-----------------*\n");
		return toret.toString();
	}
	
	public FilesystemCache(Context ctx, String cacheName) {
		appCtx = ctx.getApplicationContext();
		Log.d("FileSystem", printEnvDirs());
		root = new File(appCtx.getCacheDir(), cacheName);
		//root = new File(Environment.getExternalStorageDirectory(), cacheName);
		if (!root.exists()) {
			root.mkdirs();
		} else if (!root.isDirectory()) {
			// il nome per la cache e' usato da un file...
			throw new RuntimeException("Filename already in use.");
		}
	}

	public static final boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	protected File targetFileFor(String name) {
		return new File(root, name);
	}
	
	public boolean hasEntryFor(String name) {
		File f = targetFileFor(name);
		return f != null && f.exists();
	}
	
	public void save(String name, InputStream src) throws IOException {
		File file = targetFileFor(name);
		File temp = File.createTempFile("FSCACHE", null, root);
		FileOutputStream output = new FileOutputStream(temp);
		FileUtils.copy(src, output);
		output.flush();
		output.close();
		temp.renameTo(file);
		cacheItemSaved(name);
	}

	protected void cacheItemSaved(String name) {
		//to extend
	}

	public InputStream loadInputStream(String name) throws FileNotFoundException {
		return new FileInputStream(targetFileFor(name));
	}
}

