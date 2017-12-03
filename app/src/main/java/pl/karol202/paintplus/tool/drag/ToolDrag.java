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

package pl.karol202.paintplus.tool.drag;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerDrag;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolBottomBar;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolDrag extends StandardTool
{
	private boolean oneAxis;
	
	private int oldLayerX;
	private int oldLayerY;
	private float oldTouchX;
	private float oldTouchY;
	
	private Rect dirtyRect;
	
	public ToolDrag(Image image)
	{
		super(image);
		this.oneAxis = false;
		
		dirtyRect = new Rect();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_drag;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_drag_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return DragProperties.class;
	}
	
	@Override
	public Class<? extends ToolBottomBar> getBottomBarFragmentClass()
	{
		return null;
	}
	
	@Override
	public ToolCoordinateSpace getCoordinateSpace()
	{
		return ToolCoordinateSpace.IMAGE_SPACE;
	}
	
	@Override
	public boolean isUsingSnapping()
	{
		return false;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		x -= image.getViewX();
		y -= image.getViewY();
		
		layer = image.getSelectedLayer();
		oldLayerX = layer.getX();
		oldLayerY = layer.getY();
		oldTouchX = x;
		oldTouchY = y;
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		x -= image.getViewX();
		y -= image.getViewY();
		
		int deltaTouchX = Math.round(x - oldTouchX);
		int deltaTouchY = Math.round(y - oldTouchY);
		
		if(oneAxis)
		{
			if(Math.abs(deltaTouchX) >= Math.abs(deltaTouchY)) deltaTouchY = 0;
			else deltaTouchX = 0;
		}
		
		PointF snapped = new PointF(oldLayerX + deltaTouchX, oldLayerY + deltaTouchY);
		helpersManager.snapPoint(snapped);
		
		layer.setPosition((int) snapped.x, (int) snapped.y);
		
		expandDirtyRect();
		return true;
	}
	
	private void expandDirtyRect()
	{
		dirtyRect.left = Math.min(dirtyRect.left, layer.getX());
		dirtyRect.top = Math.min(dirtyRect.top, layer.getY());
		dirtyRect.right = Math.max(dirtyRect.right, layer.getX() + layer.getWidth());
		dirtyRect.bottom = Math.max(dirtyRect.bottom, layer.getY() + layer.getHeight());
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		int deltaX = Math.round(layer.getX() - oldLayerX);
		int deltaY = Math.round(layer.getY() - oldLayerY);
		
		ActionLayerDrag action = new ActionLayerDrag(image);
		action.setLayerAndDragDelta(layer, deltaX, deltaY);
		action.applyAction();
		
		return true;
	}
	
	@Override
	public boolean providesDirtyRegion()
	{
		return true;
	}
	
	@Override
	public Rect getDirtyRegion()
	{
		return dirtyRect;
	}
	
	@Override
	public void resetDirtyRegion()
	{
		dirtyRect.setEmpty();
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return false;
	}
	
	@Override
	public boolean doesOnTopDraw()
	{
		return false;
	}
	
	@Override
	public ToolCoordinateSpace getOnLayerDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public ToolCoordinateSpace getOnTopDrawingCoordinateSpace()
	{
		return null;
	}
	
	@Override
	public void onLayerDraw(Canvas canvas) { }
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	boolean isOneAxis()
	{
		return oneAxis;
	}
	
	void setOneAxis(boolean oneAxis)
	{
		this.oneAxis = oneAxis;
	}
}