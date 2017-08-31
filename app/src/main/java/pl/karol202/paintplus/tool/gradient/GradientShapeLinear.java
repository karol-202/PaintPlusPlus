package pl.karol202.paintplus.tool.gradient;

import android.graphics.*;
import pl.karol202.paintplus.R;

class GradientShapeLinear extends GradientShape
{
	GradientShapeLinear(ToolGradient toolGradient)
	{
		super(toolGradient);
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
	Shader createShader()
	{
		return new LinearGradient(getFirstPoint().x, getFirstPoint().y, getSecondPoint().x, getSecondPoint().y,
								  getColorsArray(), getPositionsArray(), getTileMode());
	}
}