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

import android.graphics.Bitmap;
import android.support.media.ExifInterface;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.FlipDirection;
import pl.karol202.paintplus.image.Image.RotationAmount;
import pl.karol202.paintplus.recent.OnFileEditListener;

public class OptionFileOpen extends OptionOpen
{
	private static final int REQUEST_OPEN_FILE = 1;
	
	private OnFileEditListener listener;
	
	public OptionFileOpen(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image, asyncManager);
		this.listener = listener;
	}
	
	@Override
	int getRequestId()
	{
		return REQUEST_OPEN_FILE;
	}
	
	@Override
	public void execute()
	{
		if(getImage().wasModifiedSinceLastSave()) askAboutChanges();
		else executeWithoutAsking();
	}
	
	private void askAboutChanges()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_are_you_sure);
		dialogBuilder.setMessage(R.string.dialog_unsaved_changes);
		dialogBuilder.setPositiveButton(R.string.dialog_open_file_positive, (dialog, which) -> OptionFileOpen.super.execute());
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.show();
	}
	
	public void executeWithoutAsking()
	{
		super.execute();
	}
	
	@Override //Overridden only to make public
	public void openFile(Uri uri)
	{
		super.openFile(uri);
	}
	
	@Override
	public void onImageLoaded(Bitmap bitmap)
	{
		if(bitmap == null) getAppContext().createSnackbar(R.string.message_cannot_open_file, Snackbar.LENGTH_SHORT).show();
		else openImageFromBitmap(bitmap);
	}
	
	private void openImageFromBitmap(Bitmap bitmap)
	{
		getImage().openImage(bitmap);
		getImage().setLastUri(getUri());
		getImage().centerView();
		
		if(listener != null) listener.onFileEdited(getUri(), bitmap);
		askAboutExifRotation(this::rotateImage);
	}
	
	private void rotateImage(int exifOrientation)
	{
		switch(exifOrientation)
		{
		case ExifInterface.ORIENTATION_ROTATE_90:
			getImage().rotate(RotationAmount.ANGLE_90);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			getImage().rotate(RotationAmount.ANGLE_180);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			getImage().rotate(RotationAmount.ANGLE_270);
			break;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			getImage().flip(FlipDirection.HORIZONTALLY);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			getImage().flip(FlipDirection.VERTICALLY);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			getImage().rotate(RotationAmount.ANGLE_90);
			getImage().flip(FlipDirection.HORIZONTALLY);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			getImage().rotate(RotationAmount.ANGLE_270);
			getImage().flip(FlipDirection.HORIZONTALLY);
		}
	}
}