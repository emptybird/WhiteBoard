package com.sean.whiteboard.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.sean.whiteboard.DisplayUnitUtils;
import com.sean.whiteboard.R;
import com.sean.whiteboard.ScreenUtils;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * 画笔颜色选择弹窗 Created by zhangchao on 2019/2/15.
 */

public class PaintColorPopupMenu extends PopupWindow {
	private Context context;
	private View anchor;
	private View contentView;
	private RecyclerView rvColors;
	private Arrow arrow;
	private Subject<Integer> paintColorSubject;
	private int rightMargin;
	private int bottomMargin;
	private boolean initArrowPosition;

	public PaintColorPopupMenu(Context context, int width, View anchor) {
		super(context);
		this.context = context;
		this.anchor = anchor;
		//setWidth(width);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		contentView = View.inflate(context, R.layout.layout_paint_color_menu,
				null);
		rvColors = contentView.findViewById(R.id.rv_colors);
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
		rightMargin = DisplayUnitUtils.dip2px(35);
		bottomMargin = DisplayUnitUtils.dip2px(5);
		paintColorSubject = BehaviorSubject.create();
		int spanCount = 4;
		rvColors.setLayoutManager(new GridLayoutManager(context, spanCount));
		rvColors.setHasFixedSize(true);
		rvColors.setAdapter(new ColorAdapter(context, paintColorSubject));
		rvColors.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(@NonNull Rect outRect,
					@NonNull View view, @NonNull RecyclerView parent,
					@NonNull RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				int position = parent.getChildAdapterPosition(view);
				outRect.left = position % spanCount != 0 ? DisplayUnitUtils
						.dip2px(30) : 0;
				outRect.bottom = position < spanCount ? DisplayUnitUtils
						.dip2px(15) : 0;

			}
		});
	}

	public void show() {
		int[] location = calculatePosition();
		showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]);
	}

	public Observable<Integer> paintColorObservable() {
		return paintColorSubject;
	}

	private int[] calculatePosition() {
		final int contentLocation[] = new int[2];
		final int anchorLocation[] = new int[2];
		// 获取锚点View在屏幕上的左上角坐标位置
		anchor.getLocationOnScreen(anchorLocation);
		// 获取锚点view宽高
		final int anchorHeight = anchor.getHeight();
		final int anchorWidth = anchor.getWidth();
		// 获取屏幕的高度
		final int screenWidth = ScreenUtils.getScreenWidth(context);
		// 计算contentView的高度
		contentView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		final int contentHeight = contentView.getMeasuredHeight();
		final int contentWidth = contentView.getMeasuredWidth();
		contentLocation[0] = screenWidth - contentWidth - rightMargin;
		contentLocation[1] = anchorLocation[1] - contentHeight - bottomMargin;

		if (!initArrowPosition) {
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) arrow
					.getLayoutParams();
			layoutParams.leftMargin = anchorWidth * 4
					+ (anchorWidth - layoutParams.width) / 2
					- (screenWidth - contentWidth - rightMargin);
			arrow.setLayoutParams(layoutParams);
			initArrowPosition = true;
		}
		return contentLocation;
	}

	private static class ColorHolder extends RecyclerView.ViewHolder {
		RoundRect colorView;

		ColorHolder(@NonNull View itemView) {
			super(itemView);
			colorView = (RoundRect) itemView;
		}
	}

	private static class ColorAdapter extends RecyclerView.Adapter<ColorHolder> {
		private Context context;
		private String[] colors;
		private Subject<Integer> paintColorSubject;
		private int width, height;

		ColorAdapter(Context context, Subject<Integer> paintColorSubject) {
			this.context = context;
			colors = context.getResources().getStringArray(
					R.array.paint_color_array);
			this.paintColorSubject = paintColorSubject;
			width = height = DisplayUnitUtils.dip2px(30);
		}

		@NonNull
		@Override
		public ColorHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
				int i) {
			RoundRect roundRect = new RoundRect(context);
			roundRect
					.setLayoutParams(new ViewGroup.LayoutParams(width, height));
			ColorHolder colorHolder = new ColorHolder(roundRect);
			roundRect.setOnClickListener(v -> paintColorSubject.onNext(Color
					.parseColor(colors[colorHolder.getAdapterPosition()])));
			return colorHolder;
		}

		@Override
		public void onBindViewHolder(@NonNull ColorHolder colorHolder, int i) {
			colorHolder.colorView.setColor(Color.parseColor(colors[i]));
		}

		@Override
		public int getItemCount() {
			return colors.length;
		}
	}
}
