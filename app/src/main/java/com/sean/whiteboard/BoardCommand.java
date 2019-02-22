package com.sean.whiteboard;

import android.support.annotation.IntDef;

/**
 * Created by zhangchao on 2019/2/13.
 */

@IntDef({ BoardCommand.DRAW, BoardCommand.ERASE, BoardCommand.ZOOM,
		BoardCommand.DRAG })
public @interface BoardCommand {
	int DRAW = 0; // 绘制
	int ERASE = 1; // 擦除
	int ZOOM = 2; // 缩放
	int DRAG = 3;// 拖动
}
