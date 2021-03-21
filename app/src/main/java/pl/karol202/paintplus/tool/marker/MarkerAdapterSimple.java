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

package pl.karol202.paintplus.tool.marker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

abstract class MarkerAdapterSimple implements MarkerAdapter
{
	private ToolMarker marker;
	private ColorsSet colors;

	private Paint pathPaint;
	Path path;
	private Paint ovalPaint;
	private RectF oval;

	private boolean pathCreated;

	MarkerAdapterSimple(ToolMarker marker)
	{
		this.marker = marker;
		this.colors = marker.getColors();

		this.pathPaint = new Paint();
		this.pathPaint.setStyle(Paint.Style.STROKE);
		this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
		this.pathPaint.setStrokeJoin(Paint.Join.ROUND);

		this.path = new Path();
		this.path.setFillType(Path.FillType.EVEN_ODD);

		this.ovalPaint = new Paint();

		this.oval = new RectF();
	}

	@Override
	public void onBeginDraw(float x, float y)
	{
		pathPaint.setColor(colors.getFirstColor());
		pathPaint.setAlpha((int) (marker.getOpacity() * 255));
		pathPaint.setStrokeWidth(marker.getSize());
		pathPaint.setAntiAlias(marker.isSmoothEdge());

		ovalPaint.setColor(colors.getFirstColor());
		ovalPaint.setAlpha((int) (marker.getSize() * 255));
		ovalPaint.setAntiAlias(marker.isSmoothEdge());

		path.reset();
		path.moveTo(x, y);

		pathCreated = false;
	}

	@Override
	public void onDraw(float x, float y)
	{
		pathCreated = true;
	}

	@Override
	public void onEndDraw(float x, float y)
	{
		if(pathCreated) marker.getCanvas().drawPath(path, pathPaint);
		else
		{
			oval.left = x - marker.getSize() / 2;
			oval.top = y - marker.getSize() / 2;
			oval.right = x + marker.getSize() / 2;
			oval.bottom = y + marker.getSize() / 2;
			marker.getCanvas().drawOval(oval, ovalPaint);
		}

		path.reset();
	}

	@Override
	public void onScreenDraw(Canvas canvas)
	{
		canvas.drawPath(path, pathPaint);
	}
}
