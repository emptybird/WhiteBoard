package com.sean.whiteboard.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sean.whiteboard.DisplayUnitUtils;


/**
 * 圆角矩形 Created by zhangchao on 2019/2/15.
 */

public class RoundRect extends View {
	private int radius;
	private Paint paint;

	public RoundRect(Context context) {
		this(context, null);
	}

	public RoundRect(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		radius = DisplayUnitUtils.dip2px(3);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.FILL);
	}

	public void setColor(int color) {
		paint.setColor(color);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRoundRect(0, 0, getWidth(), getHeight(),
				radius, radius, paint);
	}
}
