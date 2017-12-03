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

package pl.karol202.paintplus.tool.brush;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.StandardTool;
import pl.karol202.paintplus.tool.ToolBottomBar;
import pl.karol202.paintplus.tool.ToolCoordinateSpace;
import pl.karol202.paintplus.tool.ToolProperties;

public class ToolBrush extends StandardTool
{
	private float size;
	private float shapeOffset;
	private float opacity;
	
	private Canvas canvas;
	private ColorsSet colors;
	
	private Shader radialShader;
	private Matrix shaderMatrix;
	private Paint paint;
	private RectF oval;
	private float lastX;
	private float lastY;
	private Path path;
	private float pathDistance;
	private Rect viewDirtyRect;
	private Rect historyDirtyRect;
	private ActionLayerChange historyAction;

	public ToolBrush(Image image)
	{
		super(image);
		this.size = 25;
		this.shapeOffset = 7;
		this.opacity = 1;
		
		this.colors = image.getColorsSet();
		
		this.shaderMatrix = new Matrix();
		this.paint = new Paint();
		this.oval = new RectF();
		
		this.lastX = -1;
		this.lastY = -1;
		
		this.path = new Path();
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_brush;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_brush_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return BrushProperties.class;
	}
	
	@Override
	public Class<? extends ToolBottomBar> getBottomBarFragmentClass()
	{
		return null;
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
		
		historyAction = new ActionLayerChange(image, R.string.tool_brush);
		historyAction.setLayerChange(image.getSelectedLayerIndex(), layer.getBitmap());
		
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		
		paint.setAlpha((int) (opacity * 255));
		paint.setStrokeWidth(size);
		updateShader();
		
		path.reset();
		path.moveTo(x, y);
		
		lastX = -1;
		lastY = -1;
		
		viewDirtyRect = new Rect();
		historyDirtyRect = new Rect();
		return true;
	}
	
	private void updateShader()
	{
		int color = colors.getFirstColor();
		int center = Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
		int edge = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
		radialShader = new RadialGradient(0, 0, size / 2, center, edge, Shader.TileMode.CLAMP);
		paint.setShader(radialShader);
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		if(lastX != -1 && lastY != -1)
		{
			path.quadTo(lastX, lastY, x, y);
			lastX = -1;
			lastY = -1;
			drawPointsOnPath();
		}
		else
		{
			lastX = x;
			lastY = y;
		}
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		if(lastX != -1 && lastY != -1) path.quadTo(lastX, lastY, x, y);
		else path.lineTo(x, y);
		
		drawPointsOnPath();
		drawPoint(x, y);
		
		path.reset();
		lastX = -1;
		lastY = -1;
		pathDistance = 0;
		
		historyAction.setDirtyRect(historyDirtyRect);
		historyAction.applyAction();
		
		viewDirtyRect = null;
		historyDirtyRect = null;
		image.unlockLayers();
		return true;
	}
	
	private void drawPointsOnPath()
	{
		PathMeasure pathMeasure = new PathMeasure(path, false);
		float[] point = new float[2];
		while(pathDistance <= pathMeasure.getLength())
		{
			if(!pathMeasure.getPosTan(pathDistance, point, null)) break;
			drawPoint(point[0], point[1]);
			pathDistance += shapeOffset;
		}
	}
	
	private void drawPoint(float x, float y)
	{
		shaderMatrix.reset();
		shaderMatrix.preTranslate(x, y);
		radialShader.setLocalMatrix(shaderMatrix);
		
		oval.left = x - size / 2;
		oval.top = y - size / 2;
		oval.right = x + size / 2;
		oval.bottom = y + size / 2;
		canvas.drawOval(oval, paint);
		
		expandDirtyRectByPoint(viewDirtyRect, oval);
		expandDirtyRectByPoint(historyDirtyRect, oval);
	}
	
	private void expandDirtyRectByPoint(Rect dirtyRect, RectF point)
	{
		if(dirtyRect == null) return;
		if(dirtyRect.isEmpty()) dirtyRect.set((int) point.left, (int) point.top, (int) point.right, (int) point.bottom);
		else
		{
			dirtyRect.left = (int) Math.min(dirtyRect.left, point.left);
			dirtyRect.top = (int) Math.min(dirtyRect.top, point.top);
			dirtyRect.right = (int) Math.max(dirtyRect.right, point.right);
			dirtyRect.bottom = (int) Math.max(dirtyRect.bottom, point.bottom);
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
	
	float getSize()
	{
		return size;
	}

	void setSize(float size)
	{
		this.size = size;
	}

	float getShapeOffset()
	{
		return shapeOffset;
	}

	void setShapeOffset(float shapeOffset)
	{
		this.shapeOffset = shapeOffset;
	}
	
	float getOpacity()
	{
		return opacity;
	}
	
	void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}
}