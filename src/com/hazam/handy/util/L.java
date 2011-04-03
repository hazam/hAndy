package com.hazam.handy.util;

import android.text.TextUtils;
import android.util.Log;

/**
 * This utility class encapsulates the android Log class, adding a source
 * string for the object that originated the log call, and guarding for the log level
 * 
 * @author Emanuele Di Saverio (emanuele.disaverio at gmail.com)
 * 
 */
public class L {
	private static L defaultLogger = new L("hAndy", Log.VERBOSE);
	private final String TAG;
	private int LOGLEVEL;

	public static void setDefaultLogger(String T, int loglevel) {
		defaultLogger = new L(T, loglevel);
	}
	
	public void setLevel(int level) {
		if (level < Log.VERBOSE || level > Log.ASSERT) {
			throw new IllegalArgumentException("level is out of bounds!");
		}
		LOGLEVEL = level;
	}

	public L(String tag, int level) {
		if (TextUtils.isEmpty(tag)) {
			throw new IllegalArgumentException("tag is empty!");
		}
		TAG = tag;
		setLevel(level);
	}

	private boolean isLoggable(int level) {
		return (LOGLEVEL <= level);// && Log.isLoggable(TAG, level);
	}

	private String getSourceString(Object o) {
		if (o instanceof Class) {
			return ((Class<?>) o).getName() + " - ";
		} else {
			return o.getClass().getName() + " - ";
		}
	}

	public static void V(String message) {
		defaultLogger.v(message);
	}

	public static void V(String message, Throwable tr) {
		defaultLogger.v(message, tr);
	}

	public static void V(Object src, String message) {
		defaultLogger.v(src, message);
	}

	public static void V(Object src, String message, Throwable tr) {
		defaultLogger.v(src, message, tr);
	}

	public void v(String message) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, message);
		}
	}

	public void v(String message, Throwable tr) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, message, tr);
		}
	}

	public void v(Object src, String message) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, getSourceString(src) + message);
		}
	}

	public void v(Object src, String message, Throwable tr) {
		if (isLoggable(Log.VERBOSE)) {
			Log.v(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void D(String message) {
		defaultLogger.d(message);
	}

	public static void D(String message, Throwable tr) {
		defaultLogger.d(message, tr);
	}

	public static void D(Object src, String message) {
		defaultLogger.d(src, message);
	}

	public static void D(Object src, String message, Throwable tr) {
		defaultLogger.d(src, message, tr);
	}

	public void d(String message) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, message);
		}
	}

	public void d(String message, Throwable tr) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, message, tr);
		}
	}

	public void d(Object src, String message) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, getSourceString(src) + message);
		}
	}

	public void d(Object src, String message, Throwable tr) {
		if (isLoggable(Log.DEBUG)) {
			Log.d(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void I(String message) {
		defaultLogger.i(message);
	}

	public static void I(String message, Throwable tr) {
		defaultLogger.i(message, tr);
	}

	public static void I(Object src, String message) {
		defaultLogger.i(src, message);
	}

	public static void I(Object src, String message, Throwable tr) {
		defaultLogger.i(src, message, tr);
	}

	public void i(String message) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, message);
		}
	}

	public void i(String message, Throwable tr) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, message, tr);
		}
	}

	public void i(Object src, String message) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, getSourceString(src) + message);
		}
	}

	public void i(Object src, String message, Throwable tr) {
		if (isLoggable(Log.INFO)) {
			Log.i(TAG, getSourceString(src) + message, tr);
		}
	}

	public static void W(String message) {
		defaultLogger.w(message);
	}

	public static void W(String message, Throwable tr) {
		defaultLogger.w(message, tr);
	}

	public static void W(Object src, String message) {
		defaultLogger.w(src, message);
	}

	public static void W(Object src, String message, Throwable tr) {
		defaultLogger.w(src, message, tr);
	}

	public void w(String message) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, message);
		}
	}

	public void w(String message, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, message, tr);
		}
	}

	public void w(Object src, String message) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src) + message);
		}
	}

	public void w(Object src, String message, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src) + message, tr);
		}
	}

	public void w(Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, tr);
		}
	}

	public void w(Object src, Throwable tr) {
		if (isLoggable(Log.WARN)) {
			Log.w(TAG, getSourceString(src), tr);
		}
	}

	public static void E(String message) {
		defaultLogger.e(message);
	}

	public static void E(String message, Throwable tr) {
		defaultLogger.e(message, tr);
	}

	public static void E(Object src, String message) {
		defaultLogger.e(src, message);
	}

	public static void E(Object src, String message, Throwable tr) {
		defaultLogger.e(src, message, tr);
	}

	public void e(String message) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, message);
		}
	}

	public void e(String message, Throwable tr) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, message, tr);
		}
	}

	public void e(Object src, String message) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, getSourceString(src) + message);
		}
	}

	public void e(Object src, String message, Throwable tr) {
		if (isLoggable(Log.ERROR)) {
			Log.e(TAG, getSourceString(src) + message, tr);
		}
	}
}
