package com.hazam.handy.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
	
	public static final int DEFAULT_BUFFSIZE = 1024;
	
	public static interface Tick {
		public void tick(final long current);
	}
	
	public static void decantStreams(final InputStream in, final OutputStream out, Tick tick) throws IOException {
		decantStreams(in, out, DEFAULT_BUFFSIZE, tick);
	}
	
	public static void decantStreams(final InputStream in, final OutputStream out,
			final int buffSize, final Tick tick) throws IOException {
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
	}
}
