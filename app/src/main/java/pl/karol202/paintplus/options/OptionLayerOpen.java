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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.activity.ActivityResultListener;
import pl.karol202.paintplus.file.ImageLoaderDialog;
import pl.karol202.paintplus.file.explorer.FileExplorer;
import pl.karol202.paintplus.file.explorer.FileExplorerFactory;
import pl.karol202.paintplus.history.action.ActionLayerAdd;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

import static android.app.Activity.RESULT_OK;

public class OptionLayerOpen extends Option implements ActivityResultListener, ImageLoaderDialog.OnImageLoadListener
{
	private static final int REQUEST_OPEN_LAYER = 3;
	
	private ActivityPaint activity;
	private AsyncManager asyncManager;
	
	private Uri uri;
	
	public OptionLayerOpen(ActivityPaint activity, Image image, AsyncManager asyncManager)
	{
		super(activity, image);
		this.activity = activity;
		this.asyncManager = asyncManager;
	}
	
	@Override
	public void execute()
	{
		activity.registerActivityResultListener(REQUEST_OPEN_LAYER, this);
		
		FileExplorer explorer = FileExplorerFactory.createFileExplorer(activity);
		explorer.openFile(REQUEST_OPEN_LAYER);
	}
	
	@Override
	public void onActivityResult(int resultCode, Intent intent)
	{
		activity.unregisterActivityResultListener(REQUEST_OPEN_LAYER);
		if(resultCode != RESULT_OK) return;
		uri = intent.getData();
		
		new ImageLoaderDialog(getContext(), asyncManager, this).loadBitmapAndAskForScalingIfTooBig(uri);
	}
	
	@Override
	public void onImageLoaded(Bitmap bitmap)
	{
		if(bitmap == null)
		{
			getAppContext().createSnackbar(R.string.message_cannot_open_file, Snackbar.LENGTH_SHORT).show();
			return;
		}
		Layer layer = new Layer(0, 0, bitmap.getWidth(), bitmap.getHeight(), "xyz", Color.TRANSPARENT);
		layer.setBitmap(bitmap);
		if(!getImage().addLayer(layer, 0))
			getAppContext().createSnackbar(R.string.too_many_layers, Snackbar.LENGTH_SHORT).show();
		else createLayerAddHistoryAction(layer);
	}
	
	private void createLayerAddHistoryAction(Layer layer)
	{
		ActionLayerAdd action = new ActionLayerAdd(getImage());
		action.setLayerAfterAdding(layer);
		action.applyAction();
	}
}