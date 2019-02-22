package com.sean.whiteboard.doodle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by zhangchao on 2019/2/13.
 */

public abstract class Shape implements Cloneable{
	protected Paint paint;
	protected Path path;

	public Shape(Paint paint, Path path){
		this.paint = paint;
		this.path = path;
	}

	public abstract void onMove(float startX, float startY, float x, float y);

	public abstract void onDraw(Canvas canvas);

	public abstract boolean isSerial();

	public void reset(){

	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Shape shape = (Shape) super.clone();
		shape.path = new Path(path);
		shape.paint = new Paint(paint);
		return shape;
	}
}
