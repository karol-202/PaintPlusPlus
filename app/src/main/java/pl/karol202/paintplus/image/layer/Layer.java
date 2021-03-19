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

package pl.karol202.paintplus.image.layer;

import android.graphics.*;
import pl.karol202.paintplus.image.Image.FlipDirection;
import pl.karol202.paintplus.image.Image.OnImageChangeListener;
import pl.karol202.paintplus.image.layer.mode.DefaultLayerMode;

public class Layer
{
	private OnImageChangeListener listener;
	private Bitmap bitmap;
	private Canvas editCanvas;

	private String name;
	private boolean visible;
	private boolean temporaryHidden;
	private int x;
	private int y;
	private LegacyLayerMode mode;
	private float opacity;

	public Layer(int x, int y, String name, int width, int height, int color)
	{
		this(x, y, name, Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
		bitmap.eraseColor(color);
	}

	public Layer(int x, int y, String name, Bitmap bitmap)
	{
		this.bitmap = bitmap;
		this.editCanvas = new Canvas(bitmap);

		this.name = name;
		this.visible = true;
		this.temporaryHidden = false;
		this.x = x;
		this.y = y;
		this.mode = new DefaultLayerMode(this);
		this.opacity = 1f;
	}

	public void offset(int x, int y)
	{
		this.x += x;
		this.y += y;
	}

	public void resize(int x, int y, int width, int height)
	{
		Bitmap source = bitmap;
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		editCanvas = new Canvas(bitmap);
		editCanvas.drawBitmap(source, -x, -y, null);
		this.x += x;
		this.y += y;
	}

	public void scale(double scaleX, double scaleY, boolean bilinear)
	{
		int width = (int) Math.round(bitmap.getWidth() * scaleX);
		int height = (int) Math.round(bitmap.getHeight() * scaleY);
		scale(width, height, bilinear);
	}

	public void scale(int width, int height, boolean bilinear)
	{
		Matrix matrix = new Matrix();
		matrix.preScale((float) width / bitmap.getWidth(), (float) height / bitmap.getHeight());

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, bilinear);
		editCanvas = new Canvas(bitmap);
	}

	public void flip(FlipDirection direction)
	{
		Matrix matrix = new Matrix();
		matrix.preScale(direction == FlipDirection.HORIZONTALLY ? -1 : 1, direction == FlipDirection.VERTICALLY ? -1 : 1);

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		editCanvas = new Canvas(bitmap);
	}

	public void rotate(float angle)
	{
		rotate(angle, true);
	}

	public void rotate(float angle, boolean offset)
	{
		int oldWidth = bitmap.getWidth();
		int oldHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preRotate(angle);

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		editCanvas = new Canvas(bitmap);

		if(!offset) return;
		x -= (bitmap.getWidth() - oldWidth) / 2;
		y -= (bitmap.getHeight() - oldHeight) / 2;
	}

	public Bitmap drawLayerAndReturnBitmap(Bitmap bitmap, Canvas canvas, RectF clipRect, Matrix imageMatrix)
	{
		Matrix layerMatrix = new Matrix(imageMatrix);
		layerMatrix.preTranslate(x, y);

		mode.startDrawing(bitmap, canvas);
		if(clipRect != null) mode.setRectClipping(clipRect);
		mode.addLayer(layerMatrix);
		if(clipRect != null) mode.resetClipping();
		return mode.apply();
	}

	public void setImageChangeListener(OnImageChangeListener listener)
	{
		this.listener = listener;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		if(!bitmap.isMutable()) bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		bitmap.setHasAlpha(true);
		this.bitmap = bitmap;
		editCanvas = new Canvas(bitmap);
	}

	public Canvas getEditCanvas()
	{
		return editCanvas;
	}

	public int getWidth()
	{
		return bitmap.getWidth();
	}

	public int getHeight()
	{
		return bitmap.getHeight();
	}

	public Rect getBounds()
	{
		return new Rect(x, y, x + getWidth(), y + getHeight());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisibility(boolean visible)
	{
		this.visible = visible;
		if(listener != null) listener.onImageChanged();
	}

	public boolean isTemporaryHidden()
	{
		return temporaryHidden;
	}

	public void setTemporaryHidden(boolean temporaryHidden)
	{
		this.temporaryHidden = temporaryHidden;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		if(listener != null) listener.onImageChanged();
	}

	public LegacyLayerMode getMode()
	{
		return mode;
	}

	public void setMode(LegacyLayerMode mode)
	{
		this.mode = mode;
		mode.setLayer(this);
	}

	public float getOpacity()
	{
		return opacity;
	}

	public void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
}
