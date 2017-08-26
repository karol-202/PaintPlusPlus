package pl.karol202.paintplus.tool.gradient;

import android.graphics.RadialGradient;
import android.graphics.Shader;
import pl.karol202.paintplus.R;

class GradientShapeRadial extends GradientShape
{
	GradientShapeRadial(ToolGradient toolGradient)
	{
		super(toolGradient);
	}
	
	@Override
	int getName()
	{
		return R.string.gradient_shape_radial;
	}
	
	@Override
	int getIcon()
	{
		return R.drawable.ic_gradient_shape_radial;
	}
	
	@Override
	Shader createShader()
	{
		float radius = (float) Math.hypot(secondPoint.x - firstPoint.x, secondPoint.y - firstPoint.y);
		radius = Math.max(radius, 0.01f);
		return new RadialGradient(firstPoint.x, firstPoint.y, radius,
								  gradient.getColorsArray(), gradient.getPositionsArray(), Shader.TileMode.CLAMP);
	}
}