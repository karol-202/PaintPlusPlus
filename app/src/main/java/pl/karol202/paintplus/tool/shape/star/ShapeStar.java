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

package pl.karol202.paintplus.tool.shape.star;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.tool.shape.Join;
import pl.karol202.paintplus.tool.shape.OnShapeEditListener;
import pl.karol202.paintplus.tool.shape.Shape;
import pl.karol202.paintplus.tool.shape.ShapeProperties;
import pl.karol202.paintplus.util.Utils;

public class ShapeStar extends Shape
{
	private abstract class Dragging
	{
		private Point draggingStart;
		
		Dragging(Point draggingStart)
		{
			this.draggingStart = draggingStart;
		}
		
		abstract void drag(Point current);
		
		float getRadiusOfInscribedCircle()
		{
			float side = (float) (2 * outerRadius * Math.sin(Math.PI / corners));
			return (float) (side / (2 * Math.tan(Math.PI / corners)));
		}
		
		Point getDraggingStart()
		{
			return draggingStart;
		}
	}
	
	private class DraggingCenter extends Dragging
	{
		private Point centerAtBeginning;
		
		DraggingCenter(Point draggingStart, Point centerAtBeginning)
		{
			super(draggingStart);
			this.centerAtBeginning = centerAtBeginning;
		}
		
		@Override
		void drag(Point current)
		{
			Point delta = new Point(current);
			delta.x -= getDraggingStart().x;
			delta.y -= getDraggingStart().y;
			
			Point newCenter = new Point(centerAtBeginning);
			newCenter.offset(delta.x, delta.y);
			setCenterPoint(newCenter);
			
			createPath();
		}
	}
	
	private class DraggingOuterRadius extends Dragging
	{
		private float outerRadiusAtBeginning;
		
		DraggingOuterRadius(Point draggingStart, float outerRadiusAtBeginning)
		{
			super(draggingStart);
			this.outerRadiusAtBeginning = outerRadiusAtBeginning;
		}
		
		@Override
		void drag(Point current)
		{
			double theta = Math.toRadians(Utils.getAngle(center, current));
			float rCurrent = calcDistance(center, current.x, current.y);
			float rDraggingStart = calcDistance(center, getDraggingStart().x, getDraggingStart().y);
			float rDelta = rCurrent - rDraggingStart;
			
			float rBeginning = outerRadiusAtBeginning;
			float rResult = rBeginning + rDelta;
			
			float x = (float) (rResult * Math.cos(theta)) + center.x;
			float y = (float) (rResult * Math.sin(theta)) + center.y;
			PointF result = new PointF(x, y);
			getHelpersManager().snapPoint(result);
			
			outerRadius = calcDistance(center, (int) result.x, (int) result.y);
			
			float radiusOfInscribedCircle = getRadiusOfInscribedCircle();
			if(radiusOfInscribedCircle < innerRadius) outerRadius = getOuterRadiusByRadiusOfInscribedCircle(innerRadius);
			
			angle = (float) Utils.getAngle(center, new Point((int) result.x, (int) result.y)) - 90;
			
			createPath();
		}
		
		private float getOuterRadiusByRadiusOfInscribedCircle(float radiusOfInscribedCircle)
		{
			return (float) (radiusOfInscribedCircle * Math.tan(Math.PI / corners) / Math.sin(Math.PI / corners));
		}
	}
	
	private class DraggingInnerRadius extends Dragging
	{
		private float innerRadiusAtBeginning;
		
		DraggingInnerRadius(Point draggingStart, float innerRadiusAtBeginning)
		{
			super(draggingStart);
			this.innerRadiusAtBeginning = innerRadiusAtBeginning;
		}
		
		@Override
		void drag(Point current)
		{
			float radiusOfInscribedCircle = getRadiusOfInscribedCircle();
			
			double theta = Math.toRadians(Utils.getAngle(center, current));
			float rCurrent = calcDistance(center, current.x, current.y);
			float rDraggingStart = calcDistance(center, getDraggingStart().x, getDraggingStart().y);
			float rDelta = rCurrent - rDraggingStart;
			
			float rBeginning = innerRadiusAtBeginning;
			float rResult = rBeginning + rDelta;
			
			float x = (float) (rResult * Math.cos(theta)) + center.x;
			float y = (float) (rResult * Math.sin(theta)) + center.y;
			PointF result = new PointF(x, y);
			getHelpersManager().snapPoint(result);
			
			innerRadius = calcDistance(center, (int) result.x, (int) result.y);
			innerRadius = Math.min(innerRadius, radiusOfInscribedCircle);
			
			angle = (float) Utils.getAngle(center, new Point((int) result.x, (int) result.y)) - 90 - (180f / corners);
			
			createPath();
		}
	}
	
	private class DraggingInitial extends Dragging
	{
		DraggingInitial(Point draggingStart)
		{
			super(draggingStart);
		}
		
		@Override
		void drag(Point current)
		{
			PointF snapped = new PointF(current);
			getHelpersManager().snapPoint(snapped);
			outerRadius = calcDistance(center, (int) snapped.x, (int) snapped.y);
			innerRadius = outerRadius * getOuterToInnerRatio();
			
			angle = (float) Utils.getAngle(center, new Point((int) snapped.x, (int) snapped.y));
			
			createPath();
		}
		
		private float getOuterToInnerRatio()
		{
			return (float) Math.atan(corners * 0.24f) * 0.51f;
		}
	}
	
	private int corners;
	private boolean fill;
	private int lineWidth;
	private Join join;
	
	private boolean polygonCreated;
	private Point center;
	private float outerRadius;
	private float innerRadius;
	private float angle;
	private Path path;
	
	private Dragging dragging;
	
	public ShapeStar(Image image, OnShapeEditListener shapeEditListener)
	{
		super(image, shapeEditListener);
		this.corners = 5;
		this.fill = false;
		this.lineWidth = 30;
		this.join = Join.MITTER;
		
		update();
	}
	
	@Override
	public int getName()
	{
		return R.string.shape_star;
	}
	
	@Override
	public int getIcon()
	{
		return R.drawable.ic_shape_star_black_24dp;
	}
	
	@Override
	public Class<? extends ShapeProperties> getPropertiesClass()
	{
		return StarProperties.class;
	}
	
	public void onTouchStart(int x, int y)
	{
		Point touchPoint = new Point(x, y);
		if(!isInEditMode()) enableEditMode();
		if(!polygonCreated)
		{
			setCenterPoint(touchPoint);
			dragging = new DraggingInitial(touchPoint);
		}
		else
		{
			float centralAngle = 360f / corners;
			float halfOfCentral = centralAngle / 2;
			float angle = (float) Utils.getAngle(center, touchPoint) - this.angle;
			if(angle < 0) angle += 360;
			float angleMod = angle % centralAngle;
			float a = Math.abs(angleMod - halfOfCentral);
			float centerToSide = Utils.map(a, 0, halfOfCentral, innerRadius, outerRadius);
			
			float distanceToCenter = calcDistance(center, x, y);
			float distanceToSide = Math.abs(distanceToCenter - centerToSide);
			
			if(Math.min(distanceToCenter, distanceToSide) > getMaxTouchDistance()) dragging = null;
			else if(distanceToCenter < distanceToSide) dragging = new DraggingCenter(touchPoint, center);
			else if(a < centralAngle / 4) dragging = new DraggingInnerRadius(touchPoint, innerRadius);
			else dragging = new DraggingOuterRadius(touchPoint, outerRadius);
		}
	}
	
	public void onTouchMove(int x, int y)
	{
		Point current = new Point(x, y);
		if(dragging != null) dragging.drag(current);
	}
	
	public void onTouchStop(int x, int y)
	{
		onTouchMove(x, y);
		polygonCreated = true;
	}
	
	private void setCenterPoint(Point point)
	{
		PointF snapped = new PointF(point);
		getHelpersManager().snapPoint(snapped);
		center = new Point((int) snapped.x, (int) snapped.y);
	}
	
	@Override
	public void expandDirtyRect(Rect dirtyRect)
	{
		dirtyRect.left = Math.min(dirtyRect.left, (int) (center.x - outerRadius - lineWidth));
		dirtyRect.top = Math.min(dirtyRect.top, (int) (center.y - outerRadius - lineWidth));
		dirtyRect.right = Math.max(dirtyRect.right, (int) (center.x + outerRadius + lineWidth));
		dirtyRect.bottom = Math.max(dirtyRect.bottom, (int) (center.y + outerRadius + lineWidth));
	}
	
	@Override
	public void onScreenDraw(Canvas canvas, boolean translucent)
	{
		if(path == null) return;
		updateColor(translucent);
		canvas.drawPath(path, getPaint());
	}
	
	@Override
	public Rect getBoundsOfShape()
	{
		if(path == null) return null;
		RectF rectF = new RectF();
		path.computeBounds(rectF, false);
		Rect rect = new Rect();
		rectF.roundOut(rect);
		rect.left -= lineWidth;
		rect.top -= lineWidth;
		rect.right += lineWidth;
		rect.bottom += lineWidth;
		return rect;
	}
	
	@Override
	public void apply(Canvas imageCanvas)
	{
		if(path == null) return;
		update();
		imageCanvas.drawPath(path, getPaint());
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
		if(center != null) center.offset(x, y);
		createPath();
	}
	
	@Override
	public void update()
	{
		getPaint().setStyle(fill ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
		getPaint().setStrokeWidth(lineWidth);
		getPaint().setStrokeMiter(360);
		getPaint().setStrokeJoin(join.getPaintJoin());
		createPath();
		super.update();
	}
	
	@Override
	public void cleanUp()
	{
		polygonCreated = false;
		center = null;
		outerRadius = -1;
		innerRadius = -1;
		angle = 0;
		path = null;
		super.cleanUp();
	}
	
	@Override
	public void enableEditMode()
	{
		polygonCreated = false;
		center = null;
		outerRadius = -1;
		innerRadius = -1;
		angle = 0;
		path = null;
		super.enableEditMode();
	}
	
	private void createPath()
	{
		if(center == null || outerRadius == -1) return;
		float central = 360f / corners;
		float halfOfCentral = central / 2f;
		
		path = new Path();
		for(int i = 0; i < corners; i++)
		{
			float angleRadA = (float) Math.toRadians((central * i) + angle);
			float aX = center.x + (float) (Math.sin(angleRadA) * outerRadius);
			float aY = center.y - (float) (Math.cos(angleRadA) * outerRadius);
			if(i == 0) path.moveTo(aX, aY);
			else path.lineTo(aX, aY);
			
			float angleRadB = (float) Math.toRadians((central * i + halfOfCentral) + angle);
			float bX = center.x + (float) (Math.sin(angleRadB) * innerRadius);
			float bY = center.y - (float) (Math.cos(angleRadB) * innerRadius);
			path.lineTo(bX, bY);
		}
		path.close();
	}
	
	int getCorners()
	{
		return corners;
	}
	
	void setCorners(int corners)
	{
		if(corners < 3) throw new IllegalArgumentException("Number of corners of star cannot be lower than 3.");
		this.corners = corners;
		update();
	}
	
	boolean isFill()
	{
		return fill;
	}
	
	void setFill(boolean fill)
	{
		this.fill = fill;
		update();
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
	
	Join getJoin()
	{
		return join;
	}
	
	void setJoin(Join join)
	{
		this.join = join;
		update();
	}
}