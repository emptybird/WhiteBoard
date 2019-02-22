package com.sean.whiteboard.doodle;

import android.graphics.*;
import android.graphics.Path;

/**
 * 矩形
 * Created by zhangchao on 2019/2/14.
 */

public class Rectangle extends Shape {
	private float x;
	private float y;
	private float startX;
	private float startY;

	public Rectangle(Paint paint, Path path) {
		super(paint, path);
	}

	@Override
	public void onMove(float startX, float startY, float x, float y) {
		this.startX = startX;
		this.startY = startY;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isSerial() {
		return false;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(startX, startY, x, y, paint);
	}
}
