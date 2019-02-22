package com.sean.whiteboard.doodle;

import android.support.annotation.IntDef;

/**
 * 图形类别
 * Created by zhangchao on 2019/2/14.
 */

@IntDef({ ShapeType.PATH, ShapeType.LINE, ShapeType.RECT, ShapeType.OVAL })
public @interface ShapeType {
	int PATH = 0; // free style
	int LINE = 1; // 画线
	int RECT = 2; // 画矩形
	int OVAL = 3; // 画椭圆
}
