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

package pl.karol202.paintplus.tool.rubber;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolRubber extends StandardTool
{
	private float size;
	private float strength;
	private boolean smooth;
	
	private Canvas canvas;
	
	private Paint pathPaint;
	private Path path;
	private Paint ovalPaint;
	private float lastX;
	private float lastY;
	private boolean pathCreated;
	private boolean editStarted;
	private Rect viewDirtyRect;
	private Rect historyDirtyRect;
	
	public ToolRubber(Image image)
	{
		super(image);
		this.size = 25;
		this.strength = 1;
		this.smooth = true;
		
		this.pathPaint = new Paint();
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);
		
		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);
		
		this.ovalPaint = new Paint();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_rubber;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_rubber_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return RubberProperties.class;
	}
	
	@Override
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return true;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		image.lockLayers();
		canvas = image.getSelectedCanvas();
		if(canvas == null) return false;
		layer = image.getSelectedLayer();
		
		updateSelectionPath();
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		
		pathPaint.setColor(Color.TRANSPARENT);
		pathPaint.setAlpha((int) (strength * 255));
		pathPaint.setStrokeWidth(size);
		pathPaint.setAntiAlias(smooth);
		pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		ovalPaint.setColor(Color.TRANSPARENT);
		ovalPaint.setAlpha((int) (strength * 255));
		ovalPaint.setAntiAlias(smooth);
		ovalPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = false;
		editStarted = true;
		layer.setTemporaryHidden(true);
		
		viewDirtyRect = new Rect();
		historyDirtyRect = new Rect();
		expandDirtyRectByPoint(viewDirtyRect, (int) x, (int) y);
		expandDirtyRectByPoint(historyDirtyRect, (int) x, (int) y);
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		expandDirtyRectByPoint(viewDirtyRect, (int) x, (int) y);
		expandDirtyRectByPoint(historyDirtyRect, (int) x, (int) y);
		
		//TODO Smooth rubber
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		
		lastX = x;
		lastY = y;
		pathCreated = true;
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		expandDirtyRectByPoint(viewDirtyRect, (int) x, (int) y);
		expandDirtyRectByPoint(historyDirtyRect, (int) x, (int) y);
		
		if(lastX != -1 && lastY != -1) path.lineTo(x, y);
		
		ActionLayerChange action = new ActionLayerChange(image, R.string.tool_rubber);
		action.setLayerChange(image.getLayerIndex(layer), layer.getBitmap(), historyDirtyRect);
		
		if(pathCreated) canvas.drawPath(path, pathPaint);
		else
		{
			RectF oval = new RectF();
			oval.left = x - size / 2;
			oval.top = y - size / 2;
			oval.right = x + size / 2;
			oval.bottom = y + size / 2;
			canvas.drawOval(oval, ovalPaint);
		}
		
		action.applyAction();
		path.reset();
		lastX = -1;
		lastY = -1;
		pathCreated = false;
		editStarted = false;
		layer.setTemporaryHidden(false);
		
		viewDirtyRect = null;
		historyDirtyRect = null;
		image.unlockLayers();
		return true;
	}
	
	private void expandDirtyRectByPoint(Rect dirtyRect, int x, int y)
	{
		if(dirtyRect == null) return;
		int halfSize = (int) Math.ceil(size * 0.6f);
		if(dirtyRect.isEmpty()) dirtyRect.set(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
		else
		{
			dirtyRect.left = Math.min(dirtyRect.left, x - halfSize);
			dirtyRect.top = Math.min(dirtyRect.top, y - halfSize);
			dirtyRect.right = Math.max(dirtyRect.right, x + halfSize);
			dirtyRect.bottom = Math.max(dirtyRect.bottom, y + halfSize);
		}
	}
	
	@Override
	public boolean providesDirtyRegion()
	{
		return true;
	}
	
	@Override
	public Rect getDirtyRegion()
	{
		return viewDirtyRect;
	}
	
	@Override
	public void resetDirtyRegion()
	{
		if(viewDirtyRect != null) viewDirtyRect.setEmpty();
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return editStarted && layerVisible;
	}
	
	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}
	
	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return ToolCoordinateSpace.LAYER_SPACE;
	}
	
	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public void onLayerDraw(Canvas canvas)
	{
		resetClipping(canvas);
		doImageClipping(canvas);
		canvas.drawBitmap(layer.getBitmap(), 0, 0, null);
		doSelectionClipping(canvas);
		canvas.drawPath(path, pathPaint);
	}
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	float getSize()
	{
		return size;
	}
	
	void setSize(float size)
	{
		this.size = size;
	}
	
	float getStrength()
	{
		return strength;
	}
	
	void setStrength(float strength)
	{
		this.strength = strength;
	}
	
	boolean isSmooth()
	{
		return smooth;
	}
	
	void setSmooth(boolean smooth)
	{
		this.smooth = smooth;
	}
}