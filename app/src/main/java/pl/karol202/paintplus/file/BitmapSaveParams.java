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

package pl.karol202.paintplus.file;

import android.graphics.Bitmap;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;

import java.io.FileDescriptor;

public class BitmapSaveParams
{
	private OnBitmapSaveListener listener;
	private Bitmap bitmap;
	private FileDescriptor fileDescriptor;
	private BitmapSaveFormat format;
	
	public BitmapSaveParams(OnBitmapSaveListener listener, Bitmap bitmap, FileDescriptor fileDescriptor, BitmapSaveFormat format)
	{
		this.listener = listener;
		this.bitmap = bitmap;
		this.fileDescriptor = fileDescriptor;
		this.format = format;
	}
	
	OnBitmapSaveListener getListener()
	{
		return listener;
	}
	
	Bitmap getBitmap()
	{
		return bitmap;
	}
	
	FileDescriptor getFileDescriptor()
	{
		return fileDescriptor;
	}
	
	BitmapSaveFormat getFormat()
	{
		return format;
	}
}