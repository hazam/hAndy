package com.hazam.handy.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (srcFile.exists() == false) {
			throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
		}
		if (destFile.getParentFile() != null && destFile.getParentFile().exists() == false) {
			if (destFile.getParentFile().mkdirs() == false) {
				throw new IOException("Destination '" + destFile + "' directory cannot be created");
			}
		}
		if (destFile.exists() && destFile.canWrite() == false) {
			throw new IOException("Destination '" + destFile + "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if (destFile.exists() && destFile.isDirectory()) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		FileInputStream input = new FileInputStream(srcFile);
		try {
			FileOutputStream output = new FileOutputStream(destFile);
			try {
				FileUtils.copy(input, output);
			} finally {
				FileUtils.closeQuietly(output);
			}
		} finally {
			FileUtils.closeQuietly(input);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}
		if (preserveFileDate) {
			destFile.setLastModified(srcFile.lastModified());
		}
	}

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static void closeQuietly(InputStream input) {
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static void closeQuietly(OutputStream output) {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	public static final int DEFAULT_BUFFSIZE = 1024;

	public static interface Tick {
		public void tick(final long current);
	}

	public static long copy(InputStream input, OutputStream output) throws IOException {
		return copy(input, output, DEFAULT_BUFFER_SIZE, null);
	}

	public static long copy(InputStream input, OutputStream output, int bufSize) throws IOException {
		return copy(input, output, bufSize, null);
	}

	public static long copy(final InputStream in, final OutputStream out, Tick tick) throws IOException {
		return copy(in, out, DEFAULT_BUFFSIZE, tick);
	}

	public static long copy(final InputStream in, final OutputStream out, final int buffSize, final Tick tick)
			throws IOException {
		final byte[] buffer = new byte[buffSize];
		int len1 = 0;
		long cumul = 0;
		while ((len1 = in.read(buffer)) > 0) {
			out.write(buffer, 0, len1);
			cumul += len1;
			if (tick != null) {
				tick.tick(cumul);
			}
		}
		in.close();
		return cumul;
	}
}
