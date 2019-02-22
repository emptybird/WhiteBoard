package com.sean.whiteboard.doodle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 直线 Created by zhangchao on 2019/2/13.
 */

public class Line extends Shape {

	public Line(Paint paint, Path path) {
		super(paint, path);
	}

	@Override
	public void onMove(float startX, float startY, float x, float y) {
		path.reset();
		path.moveTo(startX, startY);
		path.lineTo(x, y);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean isSerial() {
		return false;
	}

	@Override
	public void reset() {
		super.reset();
		path.reset();
	}
}
