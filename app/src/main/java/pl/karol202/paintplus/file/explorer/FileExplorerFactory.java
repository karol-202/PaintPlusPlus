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

package pl.karol202.paintplus.file.explorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

public class FileExplorerFactory
{
	public static FileExplorer createFileExplorer(Activity activity)
	{
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return new AppFileExplorer(activity);
		Intent testIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		testIntent.addCategory(Intent.CATEGORY_OPENABLE);
		testIntent.setType("image/*");
		
		if(testIntent.resolveActivity(activity.getPackageManager()) != null) return new SAFFileExplorer(activity);
		else return new AppFileExplorer(activity);
	}
}