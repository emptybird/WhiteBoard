package com.sean.whiteboard;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by zhangchao on 2019/2/19.
 */

public class DisplayUnitUtils {

	public static int dip2px(float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				Resources.getSystem().getDisplayMetrics());
	}
}
