package com.hazam.handy.fs;

import java.io.FileNotFoundException;
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

	public ImageCache(Context ctx, String cacheName) {
		super(ctx, cacheName);
	}

	@Override
	protected void cacheItemAdded(String name) {
		super.cacheItemAdded(name);
		try {
			Bitmap justLoaded = BitmapFactory.decodeStream(loadInputStream(name));
		//	justLoaded = ImageUtils.createRoundedBitmap(justLoaded, 10);
		//	justLoaded = ImageUtils.buildReflectedBitmap(justLoaded, 6, 0xAA, 0.25f);
			memcache.put(name, new SoftReference<Bitmap>(justLoaded));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
			//	loaded = ImageUtils.createRoundedBitmap(loaded, 10);
			//	loaded = ImageUtils.buildReflectedBitmap(loaded, 6, 0xAA, 0.25f);
				memcache.put(name, new SoftReference<Bitmap>(loaded));
			} catch (FileNotFoundException fnfe) {
			}
		} else {
			trace( name +"image found in soft cache!");
		}
		return loaded;
	}
	
	@Override
	public boolean hasEntryFor(String name) {
		SoftReference<Bitmap> sr = memcache.get(name);
		boolean incache = sr != null && sr.get() != null;
		return incache || super.hasEntryFor(name);
	}
}
