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

package pl.karol202.paintplus.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import pl.karol202.paintplus.helpers.HelpersManager;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

public abstract class StandardTool implements Tool
{
	protected Image image;
	protected Selection selection;
	protected HelpersManager helpersManager;
	
	private Path selectionPath;
	protected Layer layer;
	
	public StandardTool(Image image)
	{
		this.image = image;
		this.selection = image.getSelection();
		this.helpersManager = image.getHelpersManager();
	}
	
	public abstract boolean onTouchStart(float x, float y);
	
	public abstract boolean onTouchMove(float x, float y);
	
	public abstract boolean onTouchStop(float x, float y);
	
	@Override
	public boolean onTouch(MotionEvent event, Context context)
	{
		PointF point = createTouchPoint(event.getX(), event.getY());
		
		boolean result = true;
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			result = onTouchStart(point.x, point.y);
			break;
		case MotionEvent.ACTION_MOVE:
			for(int i = 0; i < event.getHistorySize(); i++)
			{
				PointF historicalPoint = createTouchPoint(event.getHistoricalX(i), event.getHistoricalY(i));
				result = result && onTouchMove(historicalPoint.x, historicalPoint.y);
			}
			result = result && onTouchMove(point.x, point.y);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			result = onTouchStop(point.x, point.y);
			break;
		}
		return result;
	}
	
	private PointF createTouchPoint(float x, float y)
	{
		PointF point = new PointF(x, y);
		transformTouchCoordinates(point);
		snapTouchCoordinates(point);
		return point;
	}
	
	private void transformTouchCoordinates(PointF point)
	{
		if(getCoordinateSpace() == ToolCoordinateSpace.SCREEN_SPACE) return;
		
		float x = (point.x / image.getZoom()) + image.getViewX();
		float y = (point.y / image.getZoom()) + image.getViewY();
		
		if(getCoordinateSpace() == ToolCoordinateSpace.LAYER_SPACE)
		{
			x -= image.getSelectedLayerX();
			y -= image.getSelectedLayerY();
		}
		
		point.x = x;
		point.y = y;
	}
	
	private void snapTouchCoordinates(PointF point)
	{
		if(isUsingSnapping()) helpersManager.snapPoint(point);
	}
	
	protected void resetClipping(Canvas canvas)
	{
		if(canvas.getSaveCount() > 0) canvas.restoreToCount(1);
	}
	
	protected void doImageClipping(Canvas canvas)
	{
		updateLayer();
		
		canvas.save();
		canvas.clipRect(-layer.getX(), -layer.getY(),
				  image.getWidth() - layer.getX(), image.getHeight() - layer.getY());
	}
	
	protected void doSelectionClipping(Canvas canvas)
	{
		if(selectionPath == null) updateSelectionPath();
		if(!selection.isEmpty()) canvas.clipPath(selectionPath);
	}
	
	protected void doLayerAndSelectionClipping(Canvas canvas)
	{
		updateLayer();
		
		if(selectionPath == null) updateSelectionPath();
		canvas.save();
		canvas.clipRect(0, 0, layer.getWidth(), layer.getHeight());
		if(!selection.isEmpty()) canvas.clipPath(selectionPath);
	}
	
	protected void updateSelectionPath()
	{
		selectionPath = new Path(image.getSelection().getPath());
		selectionPath.offset(-layer.getX(), -layer.getY());
	}
	
	protected void updateLayer()
	{
		layer = image.getSelectedLayer();
	}
}