package pl.karol202.paintplus.tool.gradient;

import android.graphics.*;
import pl.karol202.paintplus.R;

class GradientShapeLinear extends GradientShape
{
	private Gradient gradient;
	private PointF firstPoint;
	private PointF secondPoint;
	
	private Paint paint;
	private Shader shader;
	
	GradientShapeLinear(ToolGradient toolGradient)
	{
		super(toolGradient);
		paint = new Paint();
	}
	
	@Override
	int getName()
	{
		return R.string.gradient_shape_linear;
	}
	
	@Override
	int getIcon()
	{
		return R.drawable.ic_gradient_shape_linear;
	}
	
	@Override
	void applyGradient(Canvas imageCanvas)
	{
		if(!toolGradient.canDrawGradient()) return;
		updatePaint();
		drawGradient(imageCanvas);
	}
	
	@Override
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
			shader = new LinearGradient(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y,
										gradient.getColorsArray(), gradient.getPositionsArray(), Shader.TileMode.CLAMP);
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