package pl.karol202.paintplus.tool.gradient;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class GradientPreviewView extends View
{
	private Gradient gradient;
	
	private Paint paint;
	private Shader shader;
	
	public GradientPreviewView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if(shader == null) return;
		canvas.drawRect(1, 1, getWidth() - 1, getHeight() - 1, paint);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		if(gradient != null && shader == null) updateShader();
		System.out.println("layout");
	}
	
	private void updateShader()
	{
		shader = new LinearGradient(1, 1, getWidth() - 1, 1, gradient.getColorsArray(), gradient.getPositionsArray(), Shader.TileMode.CLAMP);
		paint.setShader(shader);
	}
	
	public void setGradient(Gradient gradient)
	{
		this.gradient = gradient;
		this.shader = null;
	}
}