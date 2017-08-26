package pl.karol202.paintplus.tool.gradient;

import android.graphics.*;
import pl.karol202.paintplus.R;

class GradientShapeBilinear extends GradientShape
{
	GradientShapeBilinear(ToolGradient toolGradient)
	{
		super(toolGradient);
	}
	
	@Override
	int getName()
	{
		return R.string.gradient_shape_bilinear;
	}
	
	@Override
	int getIcon()
	{
		return R.drawable.ic_gradient_shape_bilinear;
	}
	
	@Override
	Shader createShader()
	{
		int[] oldColors = gradient.getColorsArray();
		float[] oldPositions = gradient.getPositionsArray();
		for(int i = 0; i < oldPositions.length; i++) oldPositions[i] = oldPositions[i] / 2;
		
		if(oldColors.length != oldPositions.length) throw new RuntimeException("Corrupted gradient.");
		int oldLength = oldColors.length;
		int newLength = (oldLength * 2) - 1;
		
		int[] newColors = new int[newLength];
		float[] newPositions = new float[newLength];
		
		System.arraycopy(oldColors, 0, newColors, 0, oldLength);
		System.arraycopy(oldPositions, 0, newPositions, 0, oldLength);
		for(int i = oldLength; i < newLength; i++)
		{
			newColors[i] = oldColors[newLength - i - 1];
			newPositions[i] = 1 - oldPositions[newLength - i - 1];
		}
		
		return new LinearGradient(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y,
								  newColors, newPositions, Shader.TileMode.CLAMP);
	}
}