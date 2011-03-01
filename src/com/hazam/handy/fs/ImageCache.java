package com.hazam.handy.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageCache extends FilesystemCache {

	private static final String TAG = "RemoteImageView";
	private Map<String, SoftReference<Bitmap>> memcache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	private OnSaveFilter onSaveFilter;
	private OnLoadFilter onLoadFilter;

	public ImageCache(Context ctx, String cacheName) {
		super(ctx, cacheName);
	}

	@Override
	protected void cacheItemSaved(String name) {
		super.cacheItemSaved(name);
		try {
			loadFromDisk(name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setOnSaveFilter(OnSaveFilter filter) {
		this.onSaveFilter = filter;
	}

	public void setOnLoadFilter(OnLoadFilter filter) {
		this.onLoadFilter = filter;
	}

	private static void trace(String msg) {
		Log.v(TAG, msg);
	}

	private Bitmap loadFromDisk(String name) throws FileNotFoundException {
		Bitmap loaded = null;
		if ( onLoadFilter == null) {
			loaded = BitmapFactory.decodeStream(loadInputStream(name));
		} else {
			loaded = onLoadFilter.loadResolve(name, loadInputStream(name));
		}
		memcache.put(name, new SoftReference<Bitmap>(loaded));
		return loaded;
	}
	
	public Bitmap getBitmap(String name) {
		SoftReference<Bitmap> sr = memcache.get(name);
		Bitmap loaded = sr != null ? sr.get() : null;
		if (loaded == null) {
			try {
				loaded = loadFromDisk(name);
			} catch (FileNotFoundException fnfe) {
			}
		} else {
			trace(name + "image found in soft cache!");
		}
		return loaded;
	}

	@Override
	public boolean hasEntryFor(String name) {
		SoftReference<Bitmap> sr = memcache.get(name);
		boolean incache = sr != null && sr.get() != null;
		return incache || super.hasEntryFor(name);
	}


	@Override
	public void save(String name, InputStream src) throws IOException {
		File file = targetFileFor(name);
		File temp = File.createTempFile("FSCACHE", null, root);
		FileOutputStream output = new FileOutputStream(temp);
		if (onSaveFilter != null) {
			onSaveFilter.saveResolve(name, src, output);
		} else {
			FileUtils.copy(src, output);
		}
		output.flush();
		output.close();
		temp.renameTo(file);
		cacheItemSaved(name);
	}

	public static interface OnSaveFilter {
		public void saveResolve(String name, InputStream src, OutputStream out);
	}

	public static interface OnLoadFilter {
		public Bitmap loadResolve(String name, InputStream src);
	}
}
