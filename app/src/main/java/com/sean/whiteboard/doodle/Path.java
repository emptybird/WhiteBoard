package com.sean.whiteboard.doodle;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by zhangchao on 2019/2/14.
 */

public class Path extends Shape {
	private float preX;
	private float preY;

	public Path(Paint paint, android.graphics.Path path) {
		super(paint, path);
	}

	@Override
	public void onMove(float startX, float startY, float x, float y) {
		if (preX == 0 && preY == 0) {
			preX = startX;
			preY = startY;
		}
		path.quadTo(preX, preY, x, y);
		preX = x;
		preY = y;
	}

	@Override
	public boolean isSerial() {
		return true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public void reset() {
		super.reset();
		path.reset();
		preX = 0;
		preY = 0;
	}
}
