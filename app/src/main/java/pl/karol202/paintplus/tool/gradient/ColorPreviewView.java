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

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import pl.karol202.paintplus.R;

public class ColorPreviewView extends View
{
	private Rect checkerboardRect;
	private Paint checkerboardPaint;
	
	private Paint paint;
	
	public ColorPreviewView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		Bitmap checkerboard = BitmapFactory.decodeResource(getResources(), R.drawable.checkerboard);
		Matrix checkerboardMatrix = new Matrix();
		Shader checkerboardShader = new BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		checkerboardMatrix.preTranslate(-5, -7);
		checkerboardShader.setLocalMatrix(checkerboardMatrix);
		checkerboardPaint = new Paint();
		checkerboardPaint.setShader(checkerboardShader);
		checkerboardPaint.setFilterBitmap(false);
		
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		drawCheckerboard(canvas);
		drawColor(canvas);
	}
	
	private void drawCheckerboard(Canvas canvas)
	{
		if(checkerboardRect == null) updateCheckerboardRect();
		canvas.drawRect(checkerboardRect, checkerboardPaint);
	}
	
	private void updateCheckerboardRect()
	{
		checkerboardRect = new Rect(1, 1, getWidth() - 1, getHeight() - 1);
	}
	
	private void drawColor(Canvas canvas)
	{
		canvas.drawRect(1, 1, canvas.getWidth() - 1, canvas.getHeight() - 1, paint);
	}
	
	void setColor(int color)
	{
		paint.setColor(color);
		invalidate();
	}
}