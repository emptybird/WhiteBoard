package com.sean.whiteboard.menu;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.sean.whiteboard.DisplayUnitUtils;
import com.sean.whiteboard.R;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * 画笔宽度选择弹窗 Created by zhangchao on 2019/2/15.
 */

public class LineWidthPopupMenu extends PopupWindow {
	private View anchor;
	private View contentView;
	private RadioGroup radioGroup;
	private Arrow arrow;
	private Subject<Integer> lineWidthSubject;
	private int leftMargin;
	private int bottomMargin;
	private boolean initArrowPosition;

	public LineWidthPopupMenu(Context context, int width, View anchor) {
		super(context);
		this.anchor = anchor;
		setWidth(width);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		contentView = View.inflate(context, R.layout.layout_line_width_menu,
				null);
		radioGroup = contentView.findViewById(R.id.radio_group);
		arrow = contentView.findViewById(R.id.view_arrow);
		setContentView(contentView);
		setTouchable(true);
		setFocusable(true);
		setBackgroundDrawable(new ColorDrawable());
		getContentView().setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				dismiss();
			}
		});
		initContentView();
	}

	private void initContentView() {
		leftMargin = DisplayUnitUtils.dip2px(10);
		bottomMargin = DisplayUnitUtils.dip2px(5);
		lineWidthSubject = BehaviorSubject.create();
		radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
			int lineWidth = -1;
			if (checkedId == R.id.menu_1_px) {
				lineWidth = 1;
			} else if (checkedId == R.id.menu_3_px) {
				lineWidth = 3;
			} else if (checkedId == R.id.menu_5_px) {
				lineWidth = 5;
			} else if (checkedId == R.id.menu_10_px) {
				lineWidth = 10;
			}
			lineWidthSubject.onNext(lineWidth);
		});
		radioGroup.check(R.id.menu_5_px);
	}

	public void show() {
		int[] location = calculatePosition();
		showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
	}

	public Observable<Integer> lineWidthObservable() {
		return lineWidthSubject;
	}

	private int[] calculatePosition() {
		final int contentLocation[] = new int[2];
		final int anchorLocation[] = new int[2];
		// 获取锚点View在屏幕上的左上角坐标位置
		anchor.getLocationOnScreen(anchorLocation);
		// 获取锚点view宽高
		final int anchorHeight = anchor.getHeight();
		final int anchorWidth = anchor.getWidth();

		// 计算contentView的高度
		contentView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		final int contentHeight = contentView.getMeasuredHeight();

		contentLocation[0] = leftMargin;
		contentLocation[1] = anchorLocation[1] - contentHeight - bottomMargin;

		if (!initArrowPosition) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) arrow.getLayoutParams();
			layoutParams.leftMargin = anchorWidth
					+ (anchorWidth - layoutParams.width) / 2 - leftMargin;
			arrow.setLayoutParams(layoutParams);
			initArrowPosition = true;
		}
		return contentLocation;
	}

}
