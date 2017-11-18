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

package pl.karol202.paintplus.history.action;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

import java.util.ArrayList;
import java.util.List;

public class ActionImageScale extends Action
{
	private class SavedLayer
	{
		private Bitmap bitmap;
		private int x;
		private int y;
		
		SavedLayer(Bitmap bitmap, int x, int y)
		{
			this.bitmap = bitmap;
			this.x = x;
			this.y = y;
		}
		
		public Bitmap getBitmap()
		{
			return bitmap;
		}
		
		public void setBitmap(Bitmap bitmap)
		{
			this.bitmap = bitmap;
		}
		
		public int getX()
		{
			return x;
		}
		
		public void setX(int x)
		{
			this.x = x;
		}
		
		public int getY()
		{
			return y;
		}
		
		public void setY(int y)
		{
			this.y = y;
		}
	}
	
	private int width;
	private int height;
	private List<SavedLayer> layers;
	
	public ActionImageScale(Image image)
	{
		super(image);
		width = image.getWidth();
		height = image.getHeight();
		saveLayers(image);
		updateBitmap(image);
	}
	
	private void saveLayers(Image image)
	{
		layers = new ArrayList<>();
		for(Layer layer : image.getLayers())
			layers.add(new SavedLayer(layer.getBitmap(), layer.getX(), layer.getY()));
	}
	
	private void updateBitmap(Image image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
	}
	
	private RectF transformImageRect(Image image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image)) return false;
		updateBitmap(image);
		restore(image);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		restore(image);
		return true;
	}
	
	private void restore(Image image)
	{
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();
		image.resize(0, 0, width, height);
		width = newWidth;
		height = newHeight;
		
		for(int i = 0; i < image.getLayers().size(); i++)
		{
			Layer layer = image.getLayerAtIndex(i);
			SavedLayer saved = layers.get(i);
			
			Bitmap bitmap = layer.getBitmap();
			int x = layer.getX();
			int y = layer.getY();
			
			layer.setBitmap(saved.getBitmap());
			layer.setPosition(saved.getX(), saved.getY());
			
			saved.setBitmap(bitmap);
			saved.setX(x);
			saved.setY(y);
		}
	}
	
	@Override
	boolean canApplyAction()
	{
		return width != getImage().getWidth() || height != getImage().getHeight();
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_image_scale;
	}
}