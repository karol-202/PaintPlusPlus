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

package pl.karol202.paintplus.image;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.history.action.ActionPaste;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

public class Clipboard
{
	private Image image;
	private Selection selection;
	private String defaultLayerName;
	
	private Bitmap bitmap;
	private int left;
	private int top;
	
	Clipboard(Image image, String defaultLayerName)
	{
		this.image = image;
		this.selection = image.getSelection();
		this.defaultLayerName = defaultLayerName;
	}
	
	void cut(Layer selectedLayer)
	{
		copy(selectedLayer);
		
		ActionLayerChange action = new ActionLayerChange(image, R.string.history_action_cut);
		action.setLayerChange(image.getLayerIndex(selectedLayer), selectedLayer.getBitmap(), selection.getBounds());
		
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		Path path = new Path(selection.getPath());
		path.offset(-selectedLayer.getX(), -selectedLayer.getY());
		
		Canvas canvas = selectedLayer.getEditCanvas();
		if(canvas.getSaveCount() > 0) canvas.restoreToCount(1);
		canvas.clipPath(path);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		
		action.applyAction();
	}
	
	void copy(Layer selectedLayer)
	{
		Rect bounds = selection.getBounds();
		left = bounds.left;
		top = bounds.top;
		Path path = new Path(selection.getPath());
		path.offset(-left, -top);
		
		bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.clipPath(path);
		canvas.drawBitmap(selectedLayer.getBitmap(), -left + selectedLayer.getX(), -top + selectedLayer.getY(), null);
	}
	
	void paste()
	{
		Layer layer = image.newLayer(bitmap.getWidth(), bitmap.getHeight(), defaultLayerName);
		if(layer == null) return;
		layer.setPosition(left, top);
		layer.setBitmap(bitmap);
		
		ActionPaste action = new ActionPaste(image);
		action.setLayerAfterAdding(layer);
		action.applyAction();
	}
	
	public boolean isEmpty()
	{
		return bitmap == null;
	}
}