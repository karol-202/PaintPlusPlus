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

package pl.karol202.paintplus.tool.shape.line;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;

public class ShapeLine extends Shape
{
	private int lineWidth;
	private Cap lineCap;

	private boolean lineCreated;
	private Point start;
	private Point end;

	private int draggedIndex;
	private Point draggedPoint;
	private Point draggingStart;

	public ShapeLine(LegacyImage image, OnShapeEditListener shapeEditListener)
	{
		super(image, shapeEditListener);
		this.lineWidth = 10;
		this.lineCap = Cap.ROUND;

		update();
	}

	@Override
	public int getName()
	{
		return R.string.shape_line;
	}

	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_line_black_24dp;
	}

	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return LineProperties.class;
	}

	public void onTouchStart(int x, int y)
	{
		if(!isInEditMode()) enableEditMode();
		if(!lineCreated) setStartPoint(new Point(x, y));
		else
		{
			float distanceToStart = calcDistance(start, x, y);
			float distanceToEnd = calcDistance(end, x, y);
			if(Math.min(distanceToStart, distanceToEnd) > getMaxTouchDistance())
			{
				draggedIndex = -1;
				draggedPoint = null;
				return;
			}
			if(distanceToStart < distanceToEnd)
			{
				draggedIndex = 0;
				draggedPoint = start;
			}
			else
			{
				draggedIndex = 1;
				draggedPoint = end;
			}
			draggingStart = new Point(x, y);
		}
	}

	public void onTouchMove(int x, int y)
	{
		if(!lineCreated) setEndPoint(new Point(x, y));
		else dragPoint(new Point(x, y));
	}

	public void onTouchStop(int x, int y)
	{
		if(!lineCreated) setEndPoint(new Point(x, y));
		else dragPoint(new Point(x, y));
		lineCreated = true;
	}

	private void dragPoint(Point current)
	{
		if(draggedIndex == -1) return;

		Point delta = new Point(current);
		delta.x -= draggingStart.x;
		delta.y -= draggingStart.y;

		Point dragged = new Point(draggedPoint);
		dragged.offset(delta.x, delta.y);
		if(this.draggedIndex == 0) setStartPoint(dragged);
		else setEndPoint(dragged);
	}

	private void setStartPoint(Point point)
	{
		PointF snapped = new PointF(point);
		getHelpersManager().snapPoint(snapped);
		start = new Point((int) snapped.x, (int) snapped.y);
	}

	private void setEndPoint(Point point)
	{
		PointF snapped = new PointF(point);
		getHelpersManager().snapPoint(snapped);
		end = new Point((int) snapped.x, (int) snapped.y);
	}

	@Override
	public void expandDirtyRect(Rect dirtyRect)
	{
		if(start == null || end == null) return;
		dirtyRect.left = Math.min(dirtyRect.left, Math.min(start.x, end.x) - lineWidth);
		dirtyRect.top = Math.min(dirtyRect.top, Math.min(start.y, end.y) - lineWidth);
		dirtyRect.right = Math.max(dirtyRect.right, Math.max(start.x, end.x) + lineWidth);
		dirtyRect.bottom = Math.max(dirtyRect.bottom, Math.max(start.y, end.y) + lineWidth);
	}

	@Override
	public void onScreenDraw(Canvas canvas, boolean translucent)
	{
		if(start == null || end == null) return;
		updateColor(translucent);
		canvas.drawLine(start.x, start.y, end.x, end.y, getPaint());
	}

	@Override
	public Rect getBoundsOfShape()
	{
		return new Rect(Math.min(start.x, end.x) - lineWidth,
						Math.min(start.y, end.y) - lineWidth,
						Math.max(start.x, end.y) + lineWidth,
						Math.max(start.x, end.y) + lineWidth);
	}

	@Override
	public void apply(Canvas imageCanvas)
	{
		if(start == null || end == null) return;
		update();
		imageCanvas.drawLine(start.x, start.y, end.x, end.y, getPaint());
		cleanUp();
	}

	@Override
	public void cancel()
	{
		cleanUp();
	}

	@Override
	public void offsetShape(int x, int y)
	{
		if(start == null || end == null) return;
		start.offset(x, y);
		end.offset(x, y);
	}

	@Override
	public void update()
	{
		getPaint().setStrokeWidth(lineWidth);
		getPaint().setStrokeCap(lineCap.getPaintCap());
		super.update();
	}

	@Override
	public void cleanUp()
	{
		lineCreated = false;
		start = null;
		end = null;
		super.cleanUp();
	}

	@Override
	public void enableEditMode()
	{
		lineCreated = false;
		start = null;
		end = null;
		super.enableEditMode();
	}

	int getLineWidth()
	{
		return lineWidth;
	}

	void setLineWidth(int lineWidth)
	{
		this.lineWidth = lineWidth;
		update();
	}

	Cap getLineCap()
	{
		return lineCap;
	}

	void setLineCap(Cap lineCap)
	{
		this.lineCap = lineCap;
		update();
	}
}
