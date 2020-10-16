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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;

public class ImageLoaderDialog
{
	public interface OnImageLoadListener
	{
		void onImageLoaded(Bitmap bitmap);
	}
	
	private class ImageLoadAsyncBlocker implements AsyncBlocker
	{
		@Override
		public void cancel()
		{
			onLoadingCancel();
		}
		
		@Override
		public int getMessage()
		{
			return R.string.dialog_load_wait_message;
		}
	}
	
	private Context context;
	private OnImageLoadListener listener;
	
	private ParcelFileDescriptor fileDescriptor;
	private Point bitmapSize;
	
	private AlertDialog dialog;
	
	private AsyncManager asyncManager;
	private AsyncBlocker asyncBlocker;
	private AsyncTask<BitmapLoadParams, Void, BitmapLoadResult> asyncTask;
	
	public ImageLoaderDialog(Context context, AsyncManager asyncManager, OnImageLoadListener listener)
	{
		this.context = context;
		this.asyncManager = asyncManager;
		this.listener = listener;
	}
	
	public void loadBitmapAndAskForScalingIfTooBig(Uri uri)
	{
		this.fileDescriptor = UriUtils.createFileOpenDescriptor(context, uri);
		if(fileDescriptor == null)
		{
			if(listener != null) listener.onImageLoaded(null);
			return;
		}
		this.bitmapSize = ImageLoader.getBitmapSize(fileDescriptor.getFileDescriptor());
		
		boolean tooBig = ImageLoader.isBitmapTooBig(bitmapSize);
		if(!tooBig)
		{
			if(listener != null)
				listener.onImageLoaded(ImageLoader.openBitmapAndScaleIfNecessary(fileDescriptor.getFileDescriptor()));
			UriUtils.closeFileDescriptor(fileDescriptor);
		}
		else
		{
			bitmapSize = ImageLoader.scaleBitmapSizeIfNecessary(bitmapSize);
			showDialog();
		}
	}
	
	private void showDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_image_too_big);
		builder.setMessage(context.getString(R.string.dialog_image_too_big_question, bitmapSize.x, bitmapSize.y));
		builder.setPositiveButton(R.string.scale_down, (dialogInterface, i) -> onAccept());
		builder.setNegativeButton(R.string.cancel, null);
		dialog = builder.create();
		dialog.show();
	}
	
	private void onAccept()
	{
		asyncBlocker = new ImageLoadAsyncBlocker();
		asyncManager.block(asyncBlocker);
		BitmapLoadParams params = new BitmapLoadParams(this::onBitmapLoaded, fileDescriptor.getFileDescriptor(), bitmapSize);
		asyncTask = new BitmapLoadAsyncTask();
		asyncTask.execute(params);
	}
	
	private void onBitmapLoaded(Bitmap bitmap)
	{
		asyncManager.unblock(asyncBlocker);
		if(listener != null) listener.onImageLoaded(bitmap);
		
		UriUtils.closeFileDescriptor(fileDescriptor);
	}
	
	private void onLoadingCancel()
	{
		asyncManager.unblock(asyncBlocker);
		asyncTask.cancel(true);
		
		UriUtils.closeFileDescriptor(fileDescriptor);
	}
}