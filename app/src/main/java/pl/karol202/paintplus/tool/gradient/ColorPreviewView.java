package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

class ColorPreviewView extends View
{
	private Paint paint;
	
	ColorPreviewView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(1, 1, canvas.getWidth() - 1, canvas.getHeight() - 1, paint);
	}
	
	void setColor(int color)
	{
		paint.setColor(color);
		invalidate();
	}
}