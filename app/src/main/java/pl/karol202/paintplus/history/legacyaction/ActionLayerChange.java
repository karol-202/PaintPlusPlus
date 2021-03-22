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

package pl.karol202.paintplus.history.legacyaction;

import android.graphics.*;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerChange extends LegacyAction
{
	private int name;
	private int layerId;
	private Rect dirtyRect;
	private Bitmap bitmap;

	private Paint bitmapPaint;

	public ActionLayerChange(LegacyImage image, int name)
	{
		super(image);
		this.name = name;
		this.layerId = -1;
		createPaint();
	}

	private void createPaint()
	{
		bitmapPaint = new Paint();
		bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	}

	private void updateBitmap(LegacyImage image)
	{
		if(bitmap == null) return;
		Bitmap layerBitmap = image.getLayerAtIndex(layerId).getBitmap();
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
		getPreviewCanvas().drawBitmap(bitmap, null, transformDirtyRect(layerBitmap), bitmapPaint);
	}

	private RectF transformDirtyRect(Bitmap layerBitmap)
	{
		RectF layerRect = transformLayerRect(layerBitmap);
		Matrix matrix = new Matrix();
		matrix.postScale(layerRect.width() / layerBitmap.getWidth(), layerRect.height() / layerBitmap.getHeight());
		matrix.postTranslate(layerRect.left, layerRect.top);

		RectF transformed = new RectF(dirtyRect);
		matrix.mapRect(transformed);
		return transformed;
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image) || bitmap == null) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		Bitmap newBitmap = Bitmap.createBitmap(layer.getBitmap(), dirtyRect.left, dirtyRect.top, dirtyRect.width(), dirtyRect.height());
		layer.getEditCanvas().drawBitmap(bitmap, dirtyRect.left, dirtyRect.top, bitmapPaint);
		bitmap = newBitmap;

		updateBitmap(image);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image) || bitmap == null) return false;
		Layer layer = image.getLayerAtIndex(layerId);
		Bitmap oldBitmap = Bitmap.createBitmap(layer.getBitmap(), dirtyRect.left, dirtyRect.top, dirtyRect.width(), dirtyRect.height());
		layer.getEditCanvas().drawBitmap(bitmap, dirtyRect.left, dirtyRect.top, bitmapPaint);
		bitmap = oldBitmap;

		updateBitmap(image);
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return layerId != -1 && bitmap != null && checkIfBitmapChanged();
	}

	private boolean checkIfBitmapChanged()
	{
		if(bitmap == null) return false;
		Layer layer = getImage().getLayerAtIndex(layerId);
		Bitmap oldBitmap = Bitmap.createBitmap(layer.getBitmap(), dirtyRect.left, dirtyRect.top, dirtyRect.width(), dirtyRect.height());
		return !bitmap.sameAs(oldBitmap);
	}

	@Override
	public int getActionName()
	{
		return name;
	}

	public void setLayerChange(int layerId, Bitmap bitmapBeforeChange)
	{
		setLayerChange(layerId, bitmapBeforeChange, new Rect(0, 0, bitmapBeforeChange.getWidth(), bitmapBeforeChange.getHeight()));
	}

	public void setLayerChange(int layerId, Bitmap bitmapBeforeChange, Rect dirtyRect)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.layerId = layerId;
		this.bitmap = bitmapBeforeChange;
		setDirtyRect(dirtyRect);
		updateBitmap(getImage());
	}

	public void setDirtyRect(Rect dirtyRect)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		Layer layer = getImage().getLayerAtIndex(layerId);
		this.dirtyRect = clipRect(layer, dirtyRect);
		if(bitmap != null && isRectApplicableForThisAction(dirtyRect))
			bitmap = Bitmap.createBitmap(bitmap, dirtyRect.left, dirtyRect.top, dirtyRect.width(), dirtyRect.height());
		else bitmap = null;
	}

	private Rect clipRect(Layer layer, Rect rect)
	{
		rect.left = Math.min(Math.max(rect.left, 0), layer.getWidth() - 1);
		rect.top = Math.min(Math.max(rect.top, 0), layer.getHeight() - 1);
		rect.right = Math.min(Math.max(rect.right, 0), layer.getWidth() - 1);
		rect.bottom = Math.min(Math.max(rect.bottom, 0), layer.getHeight() - 1);
		return rect;
	}

	private boolean isRectApplicableForThisAction(Rect dirtyRect)
	{
		return dirtyRect.width() > 0 && dirtyRect.height() > 0;
	}
}
