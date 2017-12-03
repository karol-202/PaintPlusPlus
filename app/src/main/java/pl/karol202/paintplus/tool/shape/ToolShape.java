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
import android.graphics.Rect;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionLayerChange;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.*;

public class ToolShape extends StandardTool implements OnShapeEditListener, OnToolChangeListener
{
	private Shape shape;
	
	private Canvas canvas;
	
	private Shapes shapes;
	private OnShapeEditListener shapeEditListener;
	private Rect dirtyRect;
	
	public ToolShape(Image image)
	{
		super(image);
		
		this.shapes = new Shapes(image, this);
		
		setShape(shapes.getShape(0));
	}
	
	@Override
	public int getName()
	{
		return R.string.tool_shape;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_tool_shape_black_24dp;
	}
	
	@Override
	public Class<? extends ToolProperties> getPropertiesFragmentClass()
	{
		return ShapeToolProperties.class;
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
		return false;
	}
	
	@Override
	public boolean onTouchStart(float x, float y)
	{
		image.lockLayers();
		updateLayer();
		if(canvas == null) return false;
		
		updateSelectionPath();
		resetClipping(canvas);
		doLayerAndSelectionClipping(canvas);
		
		shape.onTouchStart((int) x, (int) y);
		
		expandDirtyRect();
		return true;
	}
	
	@Override
	public boolean onTouchMove(float x, float y)
	{
		shape.onTouchMove((int) x, (int) y);
		
		expandDirtyRect();
		return true;
	}
	
	@Override
	public boolean onTouchStop(float x, float y)
	{
		shape.onTouchStop((int) x, (int) y);
		
		dirtyRect = null;
		image.unlockLayers();
		return true;
	}
	
	private void expandDirtyRect()
	{
		if(dirtyRect == null) dirtyRect = new Rect();
		shape.expandDirtyRect(dirtyRect);
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
		if(dirtyRect != null) dirtyRect.setEmpty();
	}
	
	@Override
	public boolean doesOnLayerDraw(boolean layerVisible)
	{
		return layerVisible;
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
		updateLayer();
		resetClipping(canvas);
		shape.onScreenDraw(canvas, true);
		
		doLayerAndSelectionClipping(canvas);
		doImageClipping(canvas);
		shape.onScreenDraw(canvas, false);
	}
	
	@Override
	public void onTopDraw(Canvas canvas) { }
	
	@Override
	protected void updateLayer()
	{
		Layer newLayer = image.getSelectedLayer();
		if(layer == null) layer = newLayer;
		if(newLayer != layer)
			shape.offsetShape(layer.getX() - newLayer.getX(), layer.getY() - newLayer.getY());
		layer = newLayer;
		
		canvas = image.getSelectedCanvas();
	}
	
	public void apply()
	{
		ActionLayerChange action = new ActionLayerChange(image, R.string.tool_shape);
		action.setLayerChange(image.getLayerIndex(layer), layer.getBitmap(), shape.getBoundsOfShape());
		
		shape.apply(canvas);
		action.applyAction();
	}
	
	public void cancel()
	{
		shape.cancel();
	}
	
	@Override
	public void onStartShapeEditing()
	{
		if(shapeEditListener != null) shapeEditListener.onStartShapeEditing();
	}
	
	@Override
	public void onToolSelected() { }
	
	@Override
	public void onOtherToolSelected()
	{
		cancel();
	}
	
	Shapes getShapesClass()
	{
		return shapes;
	}
	
	Shape getShape()
	{
		return shape;
	}
	
	void setShape(Shape shape)
	{
		if(this.shape == shape) this.shape.cancel();
		this.shape = shape;
		image.updateImage();
	}
	
	boolean isSmoothed()
	{
		return shape.isSmooth();
	}
	
	void setSmoothed(boolean smooth)
	{
		shapes.setSmooth(smooth);
	}
	
	float getOpacity()
	{
		return shape.getOpacity();
	}
	
	void setOpacity(float opacity)
	{
		shapes.setOpacity(opacity);
	}
	
	boolean isInEditMode()
	{
		return shape.isInEditMode();
	}
	
	void setShapeEditListener(OnShapeEditListener listener)
	{
		this.shapeEditListener = listener;
	}
}