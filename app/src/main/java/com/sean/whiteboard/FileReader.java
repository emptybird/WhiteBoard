package com.sean.whiteboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.TbsReaderView;

/**
 * Created by zhangchao on 2019/2/13.
 */

public class FileReader extends FrameLayout implements
		TbsReaderView.ReaderCallback {
	private Context context;
	private TbsReaderView readerView;

	public FileReader(@NonNull Context context) {
		this(context, null);
	}

	public FileReader(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		readerView = new TbsReaderView(context, this);
		addView(readerView, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}



	@Override
	public void onCallBackAction(Integer integer, Object o, Object o1) {

	}
}
