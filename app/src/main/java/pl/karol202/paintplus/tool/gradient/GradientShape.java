package pl.karol202.paintplus.tool.gradient;

import android.graphics.Canvas;

abstract class GradientShape
{
	ToolGradient toolGradient;
	
	GradientShape(ToolGradient toolGradient)
	{
		this.toolGradient = toolGradient;
	}
	
	abstract int getName();
	
	abstract int getIcon();
	
	abstract void applyGradient(Canvas imageCanvas);
	
	abstract void onScreenDraw(Canvas canvas);
}