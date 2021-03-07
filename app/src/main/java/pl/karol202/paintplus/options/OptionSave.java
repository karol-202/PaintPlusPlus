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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import pl.karol202.paintplus.AsyncBlocker;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.*;
import pl.karol202.paintplus.file.BitmapSaveAsyncTask.OnBitmapSaveListener;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.recent.OnFileEditListener;
import pl.karol202.paintplus.util.UriExtKt;

import static android.app.Activity.RESULT_OK;

abstract class OptionSave extends Option implements ActivityResultListener, AsyncBlocker, OnBitmapSaveListener
{
	private ActivityPaint activity;
	private OnFileEditListener listener;
	private AsyncManager asyncManager;

	BitmapSaveAsyncTask asyncTask;
	Uri uri;
	BitmapSaveFormat format;
	ParcelFileDescriptor parcelFileDescriptor;

	OptionSave(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
		this.listener = listener;
	}

	abstract int getRequestId();

	abstract Bitmap getBitmapToSave();

	@Override
	public void execute()
	{
		activity.registerActivityResultListener(getRequestId(), this);

		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.saveFile(getRequestId());
	}

	public void execute(Uri uri, BitmapSaveFormat format)
	{
		this.uri = uri;
		this.format = format;
		if(format != null) saveBitmapAsynchronously();
		else if(checkFileFormat() && !showSettingsDialog()) saveBitmapAsynchronously();
	}

	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(getRequestId());
		if(resultCode != RESULT_OK) return;
		uri = intent.getData();
		if(checkFileFormat() && !showSettingsDialog()) saveBitmapAsynchronously();
	}

	private boolean checkFileFormat()
	{
		format = ImageLoader.getFormat(uri.getLastPathSegment());
		if(format == null) unsupportedFormat();
		return format != null;
	}

	private void unsupportedFormat()
	{
		UriUtils.deleteDocument(getContext(), uri);
		getAppContext().createSnackbar(R.string.message_unsupported_format, Toast.LENGTH_SHORT).show();
	}

	private boolean showSettingsDialog()
	{
		if(!format.providesSettingsDialog()) return false;

		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(format.getSettingsDialogLayout(), null);
		format.customizeSettingsDialog(view);

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(format.getSettingsDialogTitle());
		builder.setView(view);
		builder.setPositiveButton(R.string.save, (dialog, which) -> saveBitmapAsynchronously());
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
		return true;
	}

	void saveBitmapAsynchronously()
	{
		asyncManager.block(this);

		parcelFileDescriptor = UriUtils.createFileSaveDescriptor(getContext(), uri);
		BitmapSaveParams params = new BitmapSaveParams(this, getBitmapToSave(),
														parcelFileDescriptor.getFileDescriptor(), format);
		asyncTask = new BitmapSaveAsyncTask();
		asyncTask.execute(params);
	}

	@Override
	public void cancel()
	{
		asyncTask.cancel(true);
		asyncManager.unblock(this);
	}

	@Override
	public int getMessage()
	{
		return R.string.dialog_save_message;
	}

	@Override
	public void onBitmapSaved(BitmapSaveResult result)
	{
		asyncManager.unblock(this);
		UriUtils.closeFileDescriptor(parcelFileDescriptor);

		switch(result.getResult())
		{
		case SUCCESSFUL:
			if(listener != null) listener.onFileEdited(uri);
			break;
		case ERROR:
			UriUtils.deleteDocument(getContext(), uri);
			getAppContext().createSnackbar(R.string.message_cannot_save_file, Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
