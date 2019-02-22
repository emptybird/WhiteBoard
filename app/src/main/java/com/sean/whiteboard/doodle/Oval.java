package com.sean.whiteboard.doodle;

import android.graphics.*;
import android.graphics.Path;

/**
 * 椭圆 Created by zhangchao on 2019/2/14.
 */

public class Oval extends Shape {
	private RectF drawRect;

	public Oval(Paint paint, Path path) {
		super(paint, path);
		drawRect = new RectF();
	}


	@Override
	public void onMove(float startX, float startY, float x, float y) {
		drawRect.set(startX, startY, x, y);
	}

	@Override
	public boolean isSerial() {
		return false;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawOval(drawRect, paint);
	}
}
