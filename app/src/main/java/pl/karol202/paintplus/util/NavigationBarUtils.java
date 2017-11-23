/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package pl.karol202.paintplus.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class NavigationBarUtils
{
	public static int getNavigationBarHeight(Context context)
	{
		int appHeight = getAppHeight(context);
		int screenSize = getScreenHeight(context);
		
		if(appHeight < screenSize) return screenSize - appHeight;
		else return 0;
	}
	
	private static int getAppHeight(Context context)
	{
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}
	
	private static int getScreenHeight(Context context)
	{
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		
		if(Build.VERSION.SDK_INT >= 17)
		{
			Point size = new Point();
			display.getRealSize(size);
			return size.y;
		}
		else return getScreenHeightAPI16(display);
	}
	
	private static int getScreenHeightAPI16(Display display)
	{
		try
		{
			return (Integer) Display.class.getMethod("getRawHeight").invoke(display);
		}
		catch (Exception ignored) {}
		return 0;
	}
}