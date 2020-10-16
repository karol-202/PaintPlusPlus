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

package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.media.ExifInterface;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.ErrorHandler;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ImageLoaderDialog;
import pl.karol202.paintplus.file.UriUtils;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public abstract class OptionOpen extends Option implements ActivityResultListener, ImageLoaderDialog.OnImageLoadListener
{
	interface RotationNeedListener
	{
		void onRotationNeed(int exifOrientation);
	}
	
	private ActivityPaint activity;
	private AsyncManager asyncManager;
	
	private Uri uri;
	
	OptionOpen(ActivityPaint activity, Image image, AsyncManager asyncManager)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
	}
	
	abstract int getRequestId();
	
	@Override
	public void execute()
	{
		activity.registerActivityResultListener(getRequestId(), this);
		
		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.openFile(getRequestId());
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(getRequestId());
		if(resultCode != RESULT_OK) return;
		openFile(intent.getData());
	}
	
	void openFile(Uri uri)
	{
		this.uri = uri;
		new ImageLoaderDialog(getContext(), asyncManager, this).loadBitmapAndAskForScalingIfTooBig(uri);
	}
	
	void askAboutExifRotation(RotationNeedListener rotationListener)
	{
		int orientation = getExifOrientation();
		if(orientation != ExifInterface.ORIENTATION_NORMAL && orientation != ExifInterface.ORIENTATION_UNDEFINED)
			showExifDialog(rotationListener, orientation);
	}
	
	private int getExifOrientation()
	{
		try
		{
			ParcelFileDescriptor parcelFileDescriptor = UriUtils.createFileOpenDescriptor(getContext(), uri);
			InputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
			ExifInterface exif = new ExifInterface(inputStream);
			inputStream.close();
			parcelFileDescriptor.close();
			return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
		}
		catch(IOException e)
		{
			ErrorHandler.report(e);
		}
		return ExifInterface.ORIENTATION_UNDEFINED;
	}
	
	private void showExifDialog(final RotationNeedListener rotationListener, final int exifOrientation)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.dialog_exif_rotation);
		builder.setPositiveButton(R.string.rotate, (dialog, which) -> rotationListener.onRotationNeed(exifOrientation));
		builder.setNegativeButton(R.string.no, null);
		builder.show();
	}
	
	Uri getUri()
	{
		return uri;
	}
}