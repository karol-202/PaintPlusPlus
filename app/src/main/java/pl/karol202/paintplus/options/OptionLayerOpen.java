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
import android.graphics.Color;
import android.media.ExifInterface;
import android.support.design.widget.Snackbar;
import pl.karol202.paintplus.AsyncManager;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.file.UriMetadata;
import pl.karol202.paintplus.history.action.ActionLayerAdd;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.FlipDirection;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionLayerOpen extends OptionOpen
{
	private static final int REQUEST_OPEN_LAYER = 3;
	
	public OptionLayerOpen(ActivityPaint activity, Image image, AsyncManager asyncManager)
	{
		super(activity, image, asyncManager);
	}
	
	@Override
	int getRequestId()
	{
		return REQUEST_OPEN_LAYER;
	}
	
	@Override
	public void onImageLoaded(Bitmap bitmap)
	{
		if(bitmap == null) getAppContext().createSnackbar(R.string.message_cannot_open_file, Snackbar.LENGTH_SHORT).show();
		else addNewLayer(bitmap);
	}
	
	private void addNewLayer(Bitmap bitmap)
	{
		final Layer layer = new Layer(0, 0, bitmap.getWidth(), bitmap.getHeight(), getLayerName(), Color.TRANSPARENT);
		layer.setBitmap(bitmap);
		if(!getImage().addLayer(layer, 0)) getAppContext().createSnackbar(R.string.too_many_layers, Snackbar.LENGTH_SHORT).show();
		else
		{
			createLayerAddHistoryAction(layer);
			askAboutExifRotation(new RotationNeedListener() {
				@Override
				public void onRotationNeed(int exifOrientation)
				{
					rotateLayer(layer, exifOrientation);
				}
			});
		}
	}
	
	private String getLayerName()
	{
		UriMetadata metadata = new UriMetadata(getContext(), getUri());
		return metadata.getDisplayName();
	}
	
	private void createLayerAddHistoryAction(Layer layer)
	{
		ActionLayerAdd action = new ActionLayerAdd(getImage());
		action.setLayerAfterAdding(layer);
		action.applyAction();
	}
	
	private void rotateLayer(Layer layer, int exifOrientation)
	{
		switch(exifOrientation)
		{
		case ExifInterface.ORIENTATION_ROTATE_90:
			layer.rotate(90);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			layer.rotate(180);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			layer.rotate(270);
			break;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			layer.flip(FlipDirection.HORIZONTALLY);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			layer.flip(FlipDirection.VERTICALLY);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			layer.rotate(90);
			layer.flip(FlipDirection.HORIZONTALLY);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			layer.rotate(-90);
			layer.flip(FlipDirection.HORIZONTALLY);
		}
	}
}