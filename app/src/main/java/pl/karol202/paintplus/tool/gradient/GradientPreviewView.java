package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

class GradientPreviewView extends View
{
	private Gradient gradient;
	
	private Paint paint;
	private Shader shader;
	
	GradientPreviewView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(gradient == null) return;
		if(shader == null) updateShader();
		canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, paint);
	}
	
	private void updateShader()
	{
		shader = new LinearGradient(1, 1, getWidth() - 1, 1, gradient.getColorsArray(), gradient.getPositionsArray(), Shader.TileMode.CLAMP);
		paint.setShader(shader);
	}
	
	void update()
	{
		updateShader();
		invalidate();
	}
	
	void setGradient(Gradient gradient)
	{
		this.gradient = gradient;
		this.shader = null;
	}
}