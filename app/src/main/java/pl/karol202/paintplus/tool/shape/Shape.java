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

package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.LegacyImage;

public abstract class Shape
{
	private static final int MAX_TOUCH_DISTANCE_DP = 25;

	private static final float TRANSLUCENT_SHAPE_OPACITY = 0.5f;

	private boolean smooth;
	private float opacity;

	private LegacyImage image;
	private OnShapeEditListener shapeEditListener;
	private boolean editMode;
	private Paint paint;
	private ColorsSet colors;
	private HelpersManager helpersManager;

	public Shape(LegacyImage image, OnShapeEditListener shapeEditListener)
	{
		this.smooth = true;
		this.opacity = 1;

		this.image = image;
		this.shapeEditListener = shapeEditListener;
		this.paint = new Paint();
		this.colors = image.getColorsSet();
		this.helpersManager = image.getHelpersManager();
	}

	public abstract int getName();

	public abstract int getIcon();

	public abstract Class<? extends ShapeProperties> getPropertiesClass();

	public abstract void onTouchStart(int x, int y);

	public abstract void onTouchMove(int x, int y);

	public abstract void onTouchStop(int x, int y);

	public abstract void expandDirtyRect(Rect dirtyRect);

	public abstract void onScreenDraw(Canvas canvas, boolean translucent);

	public abstract Rect getBoundsOfShape();

	public abstract void apply(Canvas imageCanvas);

	public abstract void cancel();

	public abstract void offsetShape(int x, int y);

	protected float calcDistance(Point point, int x, int y)
	{
		return (float) Math.hypot(point.x - x, point.y - y);
	}

	protected void update()
	{
		updateColor(false);
		paint.setAntiAlias(smooth);
		image.updateImage();
	}

	protected void updateColor(boolean translucent)
	{
		paint.setColor(colors.getFirstColor());
		if(translucent) paint.setAlpha((int) (opacity * 255 * TRANSLUCENT_SHAPE_OPACITY));
		else paint.setAlpha((int) (opacity * 255));
	}

	protected void cleanUp()
	{
		editMode = false;
		image.updateImage();
	}

	protected float getMaxTouchDistance()
	{
		return MAX_TOUCH_DISTANCE_DP * image.SCREEN_DENSITY / image.getZoom();
	}

	protected LegacyImage getImage()
	{
		return image;
	}

	protected boolean isInEditMode()
	{
		return editMode;
	}

	protected void enableEditMode()
	{
		editMode = true;
		shapeEditListener.onStartShapeEditing();
	}

	protected Paint getPaint()
	{
		return paint;
	}

	protected HelpersManager getHelpersManager()
	{
		return helpersManager;
	}

	boolean isSmooth()
	{
		return smooth;
	}

	void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
		update();
	}

	protected float getOpacity()
	{
		return opacity;
	}

	protected void setOpacity(float opacity)
	{
		this.opacity = opacity;
		update();
	}
}
