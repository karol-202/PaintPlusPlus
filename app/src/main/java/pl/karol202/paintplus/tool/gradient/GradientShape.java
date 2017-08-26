package pl.karol202.paintplus.tool.gradient;

import android.graphics.*;

abstract class GradientShape
{
	private ToolGradient toolGradient;
	Gradient gradient;
	PointF firstPoint;
	PointF secondPoint;
	
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
		if(!toolGradient.canDrawGradient()) return;
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
		PointF newPoint = new PointF(point.x, point.y);
		return newPoint;
	}
}