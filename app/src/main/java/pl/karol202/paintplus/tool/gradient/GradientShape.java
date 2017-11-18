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

package pl.karol202.paintplus.tool.gradient;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;

abstract class GradientShape
{
	private ToolGradient toolGradient;
	private Gradient gradient;
	private PointF firstPoint;
	private PointF secondPoint;
	private boolean reverted;
	private TileMode tileMode;
	
	private Paint paint;
	private Shader shader;
	
	GradientShape(ToolGradient toolGradient)
	{
		this.toolGradient = toolGradient;
		this.paint = new Paint();
	}
	
	abstract int getName();
	
	abstract int getIcon();
	
	abstract Shader createShader();
	
	void applyGradient(Canvas imageCanvas)
	{
		if(!toolGradient.canDrawGradient()) return;
		updatePaint();
		drawGradient(imageCanvas);
	}

	void onScreenDraw(Canvas canvas)
	{
		if(isShaderOutdated()) updatePaint();
		drawGradient(canvas);
	}
	
	private void drawGradient(Canvas canvas)
	{
		if(shader == null) return;
		canvas.drawRect(0, 0, toolGradient.getLayerWidth(), toolGradient.getLayerHeight(), paint);
	}
	
	private boolean isShaderOutdated()
	{
		return !toolGradient.getGradient().equals(gradient) ||
				!toolGradient.getFirstPoint().equals(firstPoint) ||
				!toolGradient.getSecondPoint().equals(secondPoint);
	}
	
	private void updatePaint()
	{
		gradient = toolGradient.getGradient();
		firstPoint = clonePoint(toolGradient.getFirstPoint());
		secondPoint = clonePoint(toolGradient.getSecondPoint());
		reverted = toolGradient.isReverted();
		tileMode = toolGradient.getRepeatability().getTileMode();
		
		if(firstPoint == null || secondPoint == null) shader = null;
		else
		{
			shader = createShader();
			paint.setShader(shader);
		}
	}
	
	private PointF clonePoint(PointF point)
	{
		if(point == null) return null;
		return new PointF(point.x, point.y);
	}
	
	int[] getColorsArray()
	{
		return reverted ? gradient.getRevertedColorsArray() : gradient.getColorsArray();
	}
	
	float[] getPositionsArray()
	{
		return reverted ? gradient.getRevertedPositionsArray() : gradient.getPositionsArray();
	}
	
	PointF getFirstPoint()
	{
		return firstPoint;
	}
	
	PointF getSecondPoint()
	{
		return secondPoint;
	}
	
	TileMode getTileMode()
	{
		return tileMode;
	}
}