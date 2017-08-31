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
		float radius = (float) Math.hypot(getSecondPoint().x - getFirstPoint().x, getSecondPoint().y - getFirstPoint().y);
		radius = Math.max(radius, 0.01f);
		return new RadialGradient(getFirstPoint().x, getFirstPoint().y, radius,
								  getColorsArray(), getPositionsArray(), getTileMode());
	}
}