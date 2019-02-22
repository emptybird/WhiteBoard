package com.sean.whiteboard;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

/**
 * Created by zhangchao on 2019/2/13.
 */

public class MyApp extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
			@Override
			public void onCoreInitFinished() {

			}

			@Override
			public void onViewInitFinished(boolean b) {

			}
		});

		QbSdk.setTbsListener(new TbsListener() {
			@Override
			public void onDownloadFinish(int i) {
				//tbs内核下载完成回调
			}

			@Override
			public void onInstallFinish(int i) {
				//内核安装完成回调，
			}

			@Override
			public void onDownloadProgress(int i) {
				//下载进度监听
			}
		});
	}
}
