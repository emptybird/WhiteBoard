package com.sean.whiteboard;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * ScreenUtils
 * <ul>
 * <strong>Convert between dp and sp</strong>
 * </ul>
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-14
 */
public class ScreenUtils {

	private ScreenUtils() {
		throw new AssertionError();
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @return
	 */
	public static int getScreenWidth(Context context) {

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int screenHeight = dm.heightPixels;
		return screenHeight;
	}

	// View宽，高
	public static int[] getLocation(View v) {
		int[] loc = new int[4];
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		loc[0] = location[0];
		loc[1] = location[1];
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);

		loc[2] = v.getMeasuredWidth();
		loc[3] = v.getMeasuredHeight();

		// base = computeWH();
		return loc;
	}

	/**
	 * 返回当前屏幕是否为竖屏。
	 *
	 * @param context
	 * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
	 */
	public static boolean isScreenOriatationPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}


	/**
	 * 判断是否有虚拟按键http://stackoverflow.com/questions/16092431/check-for-navigation
	 * -bar
	 * */
	public static boolean hasNavBar(Context context) {
		if (!ScreenUtils.isScreenOriatationPortrait(context))
			return false;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// navigation bar was introduced in Android 4.0 (API level 14)
			Resources resources = context.getResources();
			int id = resources.getIdentifier("config_showNavigationBar",
					"bool", "android");
			if (id > 0) {
				return resources.getBoolean(id);
			} else { // Check for keys
				boolean hasMenuKey = ViewConfiguration.get(context)
						.hasPermanentMenuKey();
				boolean hasBackKey = KeyCharacterMap
						.deviceHasKey(KeyEvent.KEYCODE_BACK);
				return !hasMenuKey && !hasBackKey;
			}
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是平板 配合 isTablet 使用 isTabletDevice(this) && isTablet(this)
	 *
	 * @param activityContext
	 * @return
	 */
	public static boolean isTabletDevice(Context activityContext) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		boolean xlarge = (((activityContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) || ((activityContext
				.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE));

		// If XLarge, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (xlarge) {
			DisplayMetrics metrics = new DisplayMetrics();
			AppCompatActivity activity = (AppCompatActivity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

				// Yes, this is a tablet!
				return true;
			}
		}

		// No, this is not a tablet!
		return false;
	}

	/**
	 * 当前手机是否是横屏，是true,否false
	 *
	 * @param context
	 * @return
	 */
	public static boolean isLandScape(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否进行横竖屏切换
	 *
	 * @param activity
	 */
	public static void setLandScapeAndPortrait(AppCompatActivity activity) {
		int flag = Settings.System.getInt(activity.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0);
		if (flag != 0) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
					return;
		}
		// 强制竖屏显示
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * 获取状态栏高度
	 *
	 * @param context
	 * @return 状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		int statusBarHeight = -1;
		if (context != null) {
			int resourceId = context.getResources().getIdentifier(
					"status_bar_height", "dimen", "android");
			if (resourceId > 0) {
				statusBarHeight = context.getResources().getDimensionPixelSize(
						resourceId);
			}
		}

		return statusBarHeight;
	}

	/**
	 * 获取ActionBar高度
	 *
	 * @param activity
	 *            activity
	 * @return ActionBar高度
	 */
	public static int getActionBarHeight(Activity activity) {
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize,
				tv, true)) {
			return TypedValue.complexToDimensionPixelSize(tv.data, activity
					.getResources().getDisplayMetrics());
		}
		return 0;
	}

	/**
	 * 格式化 大小
	 *
	 * @param size
	 * @return
	 */
	public static String getPrintSize(long size) {
		// 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
		if (size < 1024) {
			return String.valueOf(size) + "B";
		} else {
			size = size / 1024;
		}
		// 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
		// 因为还没有到达要使用另一个单位的时候
		// 接下去以此类推
		if (size < 1024) {
			return String.valueOf(size) + "KB";
		} else {
			size = size / 1024;
		}
		if (size < 1024) {
			// 因为如果以MB为单位的话，要保留最后1位小数，
			// 因此，把此数乘以100之后再取余
			size = size * 100;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + "MB";
		} else {
			// 否则如果要以GB为单位的，先除于1024再作同样的处理
			size = size * 100 / 1024;
			return String.valueOf((size / 100)) + "."
					+ String.valueOf((size % 100)) + "GB";
		}
	}
}
