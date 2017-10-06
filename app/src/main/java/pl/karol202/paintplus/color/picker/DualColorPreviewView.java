package pl.karol202.paintplus.color.picker;

import android.content.Context;
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
		dividerPositionPercent = attrs.getAttributeFloatValue("app", "dividerPosition", 0.3f);
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