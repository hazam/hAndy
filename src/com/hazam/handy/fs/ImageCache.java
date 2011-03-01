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

import com.hazam.handy.graphics.ImageUtils;

public class ImageCache extends FilesystemCache {

	private static final String TAG = "RemoteImageView";
	private Map<String, SoftReference<Bitmap>> memcache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	private OnSaveFilter filter;

	public ImageCache(Context ctx, String cacheName) {
		super(ctx, cacheName);
	}

	@Override
	protected void cacheItemSaved(String name) {
		super.cacheItemSaved(name);
		try {
			Bitmap justLoaded = BitmapFactory.decodeStream(loadInputStream(name));
			// justLoaded = ImageUtils.createRoundedBitmap(justLoaded, 10);
			// justLoaded = ImageUtils.buildReflectedBitmap(justLoaded, 6, 0xAA, 0.25f);
			memcache.put(name, new SoftReference<Bitmap>(justLoaded));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setFilter(OnSaveFilter filter) {
		this.filter = filter;
	}

	private static void trace(String msg) {
		Log.v(TAG, msg);
	}

	public Bitmap getBitmap(String name) {
		SoftReference<Bitmap> sr = memcache.get(name);
		Bitmap loaded = sr != null ? sr.get() : null;
		if (loaded == null) {
			try {
				loaded = BitmapFactory.decodeStream(loadInputStream(name));
				// loaded = ImageUtils.createRoundedBitmap(loaded, 10);
				// loaded = ImageUtils.buildReflectedBitmap(loaded, 6, 0xAA, 0.25f);
				memcache.put(name, new SoftReference<Bitmap>(loaded));
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
		if (filter != null) {
			filter.saveResolve(name, src, output);
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
}
