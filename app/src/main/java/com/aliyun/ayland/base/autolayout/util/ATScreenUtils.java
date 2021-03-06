package com.aliyun.ayland.base.autolayout.util;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by zhy on 15/12/4.<br/>
 * form
 * http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels/
 * 15699681#15699681
 */
public class ATScreenUtils {
	public static int[] getScreenSize(Context context) {

		int[] size = new int[2];

		WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		int widthPixels = metrics.widthPixels;
		int heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		Log.e("xhc", "width " + widthPixels + " heigth " + heightPixels + " sdk " + Build.VERSION.SDK_INT);
//		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17){
//			try {
//				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
//				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
//			} catch (Exception ignored) {
//			}
//		}
		
	
		// includes window decorations (statusbar bar/menu bar)
		// if (Build.VERSION.SDK_INT >= 17)
		// try
		// {
		// Point realSize = new Point();
		// Display.class.getMethod("getRealSize", Point.class).invoke(d,
		// realSize);
		// widthPixels = realSize.x;
		// heightPixels = realSize.y;
		// Log.e("xhc", "> 17 width "+widthPixels+" heigth "+heightPixels);
		// } catch (Exception ignored)
		// {
		// }
		size[0] = widthPixels;
		size[1] = heightPixels;
		return size;
	}
}