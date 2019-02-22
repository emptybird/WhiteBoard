package com.sean.whiteboard.menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sean.whiteboard.DisplayUnitUtils;

/**
 * Created by zhangchao on 2019/2/15.
 */

public class Arrow extends View {
	private static final int DOWN = 0;
	private static final int UP = 1;
	private Path path;
	private Paint paint;
	private int color = Color.WHITE;
	private int direction = DOWN;

	public Arrow(Context context) {
		this(context, null);
	}

	public Arrow(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		path = new Path();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		paint.setShadowLayer(DisplayUnitUtils.dip2px(2),0,DisplayUnitUtils.dip2px(2),Color.GRAY);
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		if (direction == DOWN) {
			path.lineTo(width, 0);
			path.lineTo(width / 2, height);
			path.lineTo(0, 0);
		} else {
			path.moveTo(0, height);
			path.lineTo(width / 2, 0);
			path.lineTo(width, height);
			path.lineTo(0, height);
		}
		canvas.drawPath(path, paint);
	}
}
