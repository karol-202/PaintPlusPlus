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

package pl.karol202.paintplus.helpers;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import pl.karol202.paintplus.image.Image;

public class Grid
{
	private final int LINE_WIDTH = 1;
	private final int LINE_OFFSET_CONSTANT = 40;
	private final int LINE_OFFSET_BASE = 20;
	private final int SNAP_DISTANCE_DP = 15;
	
	private Image image;
	private float density;
	private boolean enabled;
	private boolean snapToGrid;
	
	private Paint paint;
	private int[] verticalLines;
	private int[] horizontalLines;
	private float lastZoom;
	private float lastX;
	private float lastY;
	
	Grid(Image image, Resources resources)
	{
		this.image = image;
		this.density = resources.getDisplayMetrics().density;
		this.enabled = false;
		this.snapToGrid = false;
		
		this.paint = new Paint();
		this.paint.setStrokeWidth(LINE_WIDTH);
	}
	
	private void createLines(Canvas canvas)
	{
		int offset = calculateLineOffset();
		
		float right = lastX + (canvas.getWidth() / lastZoom);
		float bottom = lastY + (canvas.getHeight() / lastZoom);
		
		int firstVerticalLineIndex = (int) Math.ceil(lastX / offset);
		int lastVerticalLineIndex = (int) Math.floor(right / offset);
		int firstHorizontalLineIndex = (int) Math.ceil(lastY / offset);
		int lastHorizontalLineIndex = (int) Math.floor(bottom / offset);
		
		verticalLines = new int[lastVerticalLineIndex - firstVerticalLineIndex + 1];
		horizontalLines = new int[lastHorizontalLineIndex - firstHorizontalLineIndex + 1];
		
		for(int vertLine = firstVerticalLineIndex; vertLine <= lastVerticalLineIndex; vertLine++)
			verticalLines[vertLine - firstVerticalLineIndex] = vertLine * offset;
		for(int horLine = firstHorizontalLineIndex; horLine <= lastHorizontalLineIndex; horLine++)
			horizontalLines[horLine - firstHorizontalLineIndex] = horLine * offset;
	}
	
	private int calculateLineOffset()
	{
		float offsetRaw = density * LINE_OFFSET_CONSTANT / lastZoom;
		int nearestOffset = LINE_OFFSET_BASE;
		for(int offset = LINE_OFFSET_BASE;
		    Math.abs(offsetRaw - offset) <= Math.abs(offsetRaw - nearestOffset);
		    offset *= 2) nearestOffset = offset;
		return nearestOffset;
	}
	
	public void onScreenDraw(Canvas canvas)
	{
		if(!enabled) return;
		if(checkForChanges()) createLines(canvas);
		drawLines(canvas);
	}
	
	private boolean checkForChanges()
	{
		boolean changes = image.getZoom() != lastZoom || image.getViewX() != lastX || image.getViewY() != lastY;
		if(changes)
		{
			lastZoom = image.getZoom();
			lastX = image.getViewX();
			lastY = image.getViewY();
		}
		return changes;
	}
	
	private void drawLines(Canvas canvas)
	{
		for(int imageX : verticalLines)
		{
			int canvasX = (int) ((imageX - lastX) * lastZoom);
			canvas.drawLine(canvasX, 0, canvasX, canvas.getHeight(), paint);
		}
		for(int imageY : horizontalLines)
		{
			int canvasY = (int) ((imageY - lastY) * lastZoom);
			canvas.drawLine(0, canvasY, canvas.getWidth(), canvasY, paint);
		}
	}
	
	float snapXToGrid(float x)
	{
		int snapDistance = (int) (SNAP_DISTANCE_DP * density);
		
		int xSquareIndex = -1;
		for(int xLine = 0; xLine < verticalLines.length && verticalLines[xLine] <= x; xSquareIndex = xLine++);
		
		int left = xSquareIndex != -1 ? verticalLines[xSquareIndex] : -1;
		int right = xSquareIndex + 1 < verticalLines.length ? verticalLines[xSquareIndex + 1] : -1;
		
		boolean leftSnap = x - left < snapDistance && left != -1;
		boolean rightSnap = right - x < snapDistance && right != -1;
		
		if(leftSnap && !rightSnap) return left;
		else if(!leftSnap && rightSnap) return right;
		else if(leftSnap && rightSnap) return (x - left) <= (right - x) ? left : right;
		else return x;
	}
	
	float snapYToGrid(float y)
	{
		int snapDistance = (int) (SNAP_DISTANCE_DP * density);
		
		int ySquareIndex = -1;
		for(int yLine = 0; yLine < horizontalLines.length && horizontalLines[yLine] <= y; ySquareIndex = yLine++);
		
		int top = ySquareIndex != -1 ? horizontalLines[ySquareIndex] : -1;
		int bottom = ySquareIndex + 1 < horizontalLines.length ? horizontalLines[ySquareIndex + 1] : -1;
		
		boolean topSnap = y - top < snapDistance && top != -1;
		boolean bottomSnap = bottom - y < snapDistance && bottom != -1;
		
		if(topSnap && !bottomSnap) return top;
		else if(!topSnap && bottomSnap) return bottom;
		else if(topSnap && bottomSnap) return (y - top) <= (bottom - y) ? top : bottom;
		else return y;
	}
	
	void snapPointToGrid(PointF point)
	{
		point.x = snapXToGrid(point.x);
		point.y = snapYToGrid(point.y);
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public boolean isSnapToGrid()
	{
		return snapToGrid;
	}
	
	public void setSnapToGrid(boolean snapToGrid)
	{
		this.snapToGrid = snapToGrid;
	}
}