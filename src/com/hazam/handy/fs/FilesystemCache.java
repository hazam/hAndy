package com.hazam.handy.fs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FilesystemCache {
	private String rootDirectory;
	private String rootOnSDCard;
	
	public FilesystemCache(String rootDirectory) {
		this.rootDirectory = rootDirectory;
		rootOnSDCard = "/sdcard/" + rootDirectory;
		
		File rootFile = new File(rootOnSDCard);
		if (!rootFile.exists()) {
			rootFile.mkdir();
		} else {
			if (!rootFile.isDirectory()) {
				// il nome per la cache e' usato da un file...
				throw new RuntimeException("Filename already in use.");
			}
		}
	}
	
	public static final boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	public void saveData(String name, byte[] data) throws IOException {
		File file = new File(rootOnSDCard + name);
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream output = new FileOutputStream(file);
		output.write(data);
		output.flush();
		output.close();
	}
	
	public byte[] loadData(String name) throws IOException {
		File file = new File(rootOnSDCard + name);
		DataInputStream input = new DataInputStream(new FileInputStream(file));
		byte[] data = new byte[(int) file.length()];
		input.readFully(data);
		return data;
	}
	
	public Bitmap loadDataAsBitmap(String name) throws FileNotFoundException {
		return BitmapFactory.decodeStream(loadDataAsInputStream(name));
	}
	
	public InputStream loadDataAsInputStream(String name) throws FileNotFoundException {
		return new FileInputStream(rootDirectory + name);
	}
}
