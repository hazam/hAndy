package com.hazam.handy.graphics;

import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;

public final class GraphicUtils {
	public static float distance(final float x1,final float x2,final float y1,final float y2) {
		final float x = x2 - x1;
		final float y = y2 - y1;
		return FloatMath.sqrt(x * x + y * y);
	}
	
	public static float distance(final PointF one,final PointF two) {
		return distance(one.x, two.x, one.y, two.y);
	}
	
	public static PointF middlePoint(final PointF one,final PointF two,final PointF resultHolder) {
		return middlePoint(one.x, two.x, one.y, two.y, resultHolder);
	}
	
	public static PointF middlePoint(final float x1,final float x2,final float y1,final float y2, PointF resultHolder) {
		final float x = x1 + x2;
		final float y = y1 + y2;
		final PointF toret = resultHolder != null ? resultHolder : new PointF();
		toret.set(x / 2, y / 2);
		return toret;
	}
}
