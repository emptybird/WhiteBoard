package com.sean.whiteboard.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.sean.whiteboard.BoardCommand;
import com.sean.whiteboard.DisplayUnitUtils;
import com.sean.whiteboard.R;
import com.sean.whiteboard.doodle.DoodleView;
import com.sean.whiteboard.doodle.ShapeType;

/**
 * 畫板菜單 Created by zhangchao on 2019/2/15.
 */

public class BoardMenu extends FrameLayout {
	private Context context;
	private RadioButton menuBrush; // 畫筆
	private RadioButton menuLineWidth; // 線寬
	private RadioButton menuStraightLine; // 直线
	private RadioButton menuEraser; // 橡皮
	private RadioButton menuPaintColor; // 颜色
	private RadioButton[] buttons;
	private BoardConfigChangeListener configChangeListener;
	private RadioButton checkedButton;
	private LineWidthPopupMenu lineWidthPopupMenu; // 线宽弹窗
	private PaintColorPopupMenu paintColorPopupMenu; // 画笔颜色弹窗

	public BoardMenu(@NonNull Context context) {
		this(context, null);
	}

	public BoardMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		View.inflate(context, R.layout.layout_board_menu, this);
		menuBrush = findViewById(R.id.menu_brush);
		menuLineWidth = findViewById(R.id.menu_line_width);
		menuStraightLine = findViewById(R.id.menu_straight_line);
		menuEraser = findViewById(R.id.menu_eraser);
		menuPaintColor = findViewById(R.id.menu_paint_color);
		buttons = new RadioButton[] { menuBrush, menuLineWidth,
				menuStraightLine, menuEraser, menuPaintColor };
		setListener();
	}

	private void setListener() {
		lineWidthPopupMenu = new LineWidthPopupMenu(context,
				DisplayUnitUtils.dip2px(240), menuLineWidth);
		lineWidthPopupMenu.lineWidthObservable()
				.filter(width -> width > 0 && configChangeListener != null)
				.subscribe(width -> {
					configChangeListener.onLineWidthChange(width);
					lineWidthPopupMenu.dismiss();
				});

		paintColorPopupMenu = new PaintColorPopupMenu(context,
				ViewGroup.LayoutParams.WRAP_CONTENT, menuPaintColor);
		paintColorPopupMenu.paintColorObservable()
				.filter(color -> configChangeListener != null)
				.subscribe(color -> {
					configChangeListener.onPaintColorChange(color);
					paintColorPopupMenu.dismiss();
				});

		menuBrush.setOnClickListener(v -> {
			setChecked(menuBrush);
			if (configChangeListener != null) {
				configChangeListener.onShapeChange(ShapeType.PATH);
			}
		});

		menuLineWidth.setOnClickListener(v -> {
			setChecked(menuLineWidth);
			lineWidthPopupMenu.show();
		});

		menuStraightLine.setOnClickListener(v -> {
			setChecked(menuStraightLine);
			if (configChangeListener != null) {
				configChangeListener.onShapeChange(ShapeType.LINE);
			}
		});

		menuEraser.setOnClickListener(v -> {
			setChecked(menuEraser);
			if (configChangeListener != null) {
				configChangeListener.onModeChange(BoardCommand.DRAG);
			}
		});

		menuPaintColor.setOnClickListener(v -> {
			setChecked(menuPaintColor);
			paintColorPopupMenu.show();
		});
	}

	private void setChecked(RadioButton menu) {
		if (checkedButton == menu) {
			return;
		}
		checkedButton = menu;
		for (RadioButton button : buttons) {
			boolean isChecked = button == checkedButton;
			button.setChecked(isChecked);
			button.setTextColor(isChecked ? getResources().getColor(
					R.color.board_menu_check_color) : getResources().getColor(
					R.color.board_menu_text_color));
		}
	}

	public void setConfigChangeListener(
			BoardConfigChangeListener configChangeListener) {
		this.configChangeListener = configChangeListener;
	}

	public interface BoardConfigChangeListener {
		void onShapeChange(@ShapeType int shapeType);

		void onLineWidthChange(int lineWidth);

		void onModeChange(@DoodleView.Mode int mode);

		void onPaintColorChange(int color);

	}

}
