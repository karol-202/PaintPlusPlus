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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import com.google.android.material.snackbar.Snackbar;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ImageLoader;
import pl.karol202.paintplus.file.UriUtils;
import pl.karol202.paintplus.image.Image;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_PICTURES;
import static android.provider.MediaStore.EXTRA_OUTPUT;

public class OptionFileCapturePhoto extends Option implements ActivityResultListener
{
	private static final int REQUEST_CAPTURE_PHOTO = 5;

	private ActivityPaint activity;
	private File photoFile;

	public OptionFileCapturePhoto(ActivityPaint activity, Image image)
	{
		super(activity, image);
		this.activity = activity;
	}

	@Override
	public void execute()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_are_you_sure);
		dialogBuilder.setMessage(R.string.dialog_unsaved_changes);
		dialogBuilder.setPositiveButton(R.string.dialog_capture_photo_positive, (dialog, which) -> capturePhoto());
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.show();
	}

	private void capturePhoto()
	{
		tryToCreatePhotoFile();

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(intent.resolveActivity(activity.getPackageManager()) == null)
			throw new RuntimeException("Cannot resolve camera activity.");

		Uri photoUri = FileProvider.getUriForFile(getContext(), "pl.karol202.paintplus", photoFile);
		intent.putExtra(EXTRA_OUTPUT, photoUri);
		activity.registerActivityResultListener(REQUEST_CAPTURE_PHOTO, this);
		activity.startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
	}

	private void tryToCreatePhotoFile()
	{
		try
		{
			photoFile = createPhotoFile();
		}
		catch(IOException ex)
		{
			throw new RuntimeException("Cannot create temporary file for photo.", ex);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private File createPhotoFile() throws IOException
	{
		String dateString = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss").format(new Date());
		String fileName = "CAPTURED_" + dateString;
		File directory = getContext().getExternalFilesDir(DIRECTORY_PICTURES);
		photoFile = File.createTempFile(fileName, ".jpeg", directory);
		return photoFile;
	}

	@Override
	public void onActivityResult(int resultCode, Intent data)
	{
		activity.unregisterActivityResultListener(REQUEST_CAPTURE_PHOTO);
		if(resultCode != RESULT_OK) return;

		Uri uri = Uri.fromFile(photoFile);
		ParcelFileDescriptor fileDescriptor = UriUtils.createFileOpenDescriptor(getContext(), uri);

		if(fileDescriptor != null) openBitmap(fileDescriptor);

		UriUtils.closeFileDescriptor(fileDescriptor);
		photoFile.delete();
	}

	private void openBitmap(ParcelFileDescriptor fileDescriptor)
	{
		Bitmap bitmap = ImageLoader.openBitmapAndScaleIfNecessary(fileDescriptor.getFileDescriptor());
		if(bitmap == null) getAppContext().createSnackbar(R.string.message_cannot_open_file, Snackbar.LENGTH_SHORT).show();
		else
		{
			getImage().openImage(bitmap);
			getImage().centerView();
		}
	}
}
