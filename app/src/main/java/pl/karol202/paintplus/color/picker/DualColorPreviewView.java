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

package pl.karol202.paintplus.color.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import pl.karol202.paintplus.R;

public class DualColorPreviewView extends View
{
	private float dividerPositionPercent;
	private int dividerPosition;
	
	private Paint checkerboardPaint;
	private Paint oldPaint;
	private Paint newPaint;
	
	public DualColorPreviewView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		obtainAttributes(context, attrs);
		dividerPosition = -1;
		
		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.checkerboard);
		Matrix checkerboardMatrix = new Matrix();
		Shader checkerboardShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkerboardMatrix.preTranslate(-5, -7);
		checkerboardShader.setLocalMatrix(checkerboardMatrix);
		checkerboardPaint = new Paint();
		checkerboardPaint.setShader(checkerboardShader);
		checkerboardPaint.setFilterBitmap(false);
		
		oldPaint = new Paint();
		newPaint = new Paint();
		newPaint.setColor(Color.RED);
	}
	
	private void obtainAttributes(Context context, AttributeSet attrs)
	{
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DualColorPreviewView, 0, 0);
		dividerPositionPercent = typedArray.getFloat(R.styleable.DualColorPreviewView_dividerPosition, 0.5f);
		typedArray.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(dividerPosition == -1) dividerPosition = Math.round(dividerPositionPercent * getWidth());
		drawCheckerboard(canvas);
		drawOldColor(canvas);
		drawNewColor(canvas);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), checkerboardPaint);
	}
	
	private void drawOldColor(Canvas canvas)
	{
		canvas.drawRect(0, 0, dividerPosition, getHeight(), oldPaint);
	}
	
	private void drawNewColor(Canvas canvas)
	{
		canvas.drawRect(dividerPosition, 0, getWidth(), getHeight(), newPaint);
	}
	
	public void setOldColor(int color)
	{
		oldPaint.setColor(color);
		invalidate();
	}
	
	public void setNewColor(int color)
	{
		newPaint.setColor(color);
		invalidate();
	}
}