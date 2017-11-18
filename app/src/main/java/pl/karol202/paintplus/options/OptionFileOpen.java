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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ImageLoaderDialog;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.recent.OnFileEditListener;

import static android.app.Activity.RESULT_OK;

public class OptionFileOpen extends Option implements ActivityResultListener, ImageLoaderDialog.OnImageLoadListener
{
	private static final int REQUEST_OPEN_FILE = 1;
	
	private ActivityPaint activity;
	private OnFileEditListener listener;
	private AsyncManager asyncManager;
	
	private Uri uri;
	
	public OptionFileOpen(ActivityPaint activity, Image image, AsyncManager asyncManager, OnFileEditListener listener)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
		this.listener = listener;
	}
	
	@Override
	public void execute()
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_are_you_sure);
		dialogBuilder.setMessage(R.string.dialog_unsaved_changes);
		dialogBuilder.setPositiveButton(R.string.dialog_open_file_positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				startFileOpenActivity();
			}
		});
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.show();
	}
	
	public void executeWithoutAsking()
	{
		startFileOpenActivity();
	}
	
	private void startFileOpenActivity()
	{
		activity.registerActivityResultListener(REQUEST_OPEN_FILE, this);
		
		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.openFile(REQUEST_OPEN_FILE);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(REQUEST_OPEN_FILE);
		if(resultCode != RESULT_OK) return;
		openFile(intent.getData());
	}
	
	public void openFile(Uri uri)
	{
		this.uri = uri;
		System.out.println(uri.getPath());
		new ImageLoaderDialog(context, asyncManager, this).loadBitmapAndAskForScalingIfTooBig(uri);
	}
	
	@Override
	public void onImageLoaded(Bitmap bitmap)
	{
		if(bitmap == null) Toast.makeText(context, R.string.message_cannot_open_file, Toast.LENGTH_SHORT).show();
		else
		{
			image.openImage(bitmap);
			image.setLastUri(uri);
			image.centerView();
			
			if(listener != null) listener.onFileEdited(uri, bitmap);
		}
	}
}