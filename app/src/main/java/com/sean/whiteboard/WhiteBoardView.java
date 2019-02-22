package com.sean.whiteboard;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sean.whiteboard.doodle.DoodleView;
import com.sean.whiteboard.menu.BoardMenu;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * 电子画板
 * Created by zhangchao on 2019/2/13.
 */

public class WhiteBoardView extends FrameLayout implements
		TbsReaderView.ReaderCallback, LifecycleObserver,
		BoardMenu.BoardConfigChangeListener {
	private String tempFilePath;
	private Context context;
	private TbsReaderView readerView; // 文件展示View
	private DoodleView doodleView; // 批注view

	@DoodleView.Mode
	private int mode = BoardCommand.DRAW; // 绘制模式 画图/拖动

	public WhiteBoardView(@NonNull Context context) {
		this(context, null);
	}

	public WhiteBoardView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		readerView = new TbsReaderView(context, this);
		addView(readerView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		tempFilePath = Environment.getExternalStorageDirectory() + "/"
				+ "TbsReaderTemp";

		doodleView = new DoodleView(context);
		addView(doodleView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		readerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				readerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				Log.e("====readerView ", "width : " + readerView.getWidth() + " height : " + readerView.getHeight());
			}
		});

	}

	public void displayFile(@NonNull File file) {
		File readerTempFile = new File(tempFilePath);
		if (!readerTempFile.exists()) {
			readerTempFile.mkdir();
		}
		Bundle localBundle = new Bundle();
		localBundle.putString("filePath", file.getPath());
		localBundle.putString("tempPath", tempFilePath);
		String fileType = getFileType(file.getName());
		boolean canRead = readerView.preOpen(fileType, false);
		if (canRead) {
			readerView.openFile(localBundle);
		} else {
			Log.e("", " ==== can not read this type : " + fileType);
		}

	}

	/***
	 * 获取文件类型
	 */
	private String getFileType(String fileName) {
		String type = "";
		if (TextUtils.isEmpty(fileName)) {
			return type;
		}
		int typeIndex = fileName.lastIndexOf('.');
		if (typeIndex <= -1) {
			return type;
		}
		type = fileName.substring(typeIndex + 1);
		return type;
	}

	@Override
	public void onCallBackAction(Integer integer, Object o, Object o1) {

	}

	@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
	public void onDestroy() {
		if (readerView != null) {
			readerView.onStop();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		if (mode == BoardCommand.DRAG){
			doodleView.onTouchEvent(ev);
		}
		return handled;
	}

	@Override
	public void onShapeChange(int shapeType) {
		doodleView.setShapeType(shapeType);
	}

	@Override
	public void onLineWidthChange(int lineWidth) {
		doodleView.setStockWidth(lineWidth);
	}

	@Override
	public void onModeChange(int mode) {
		this.mode = mode;
		doodleView.setMode(mode);
	}

	@Override
	public void onPaintColorChange(int color) {
		doodleView.setColor(color);
	}


}
