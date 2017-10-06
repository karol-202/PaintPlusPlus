package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import pl.karol202.paintplus.R;

class ColorPreviewView extends View
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