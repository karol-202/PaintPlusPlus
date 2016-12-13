package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public interface Shape
{
	int getName();
	
	int getIcon();
	
	Class<? extends ShapeProperties> getPropertiesClass();
	
	boolean onTouch(MotionEvent event);
	
	void onScreenDraw(Canvas canvas);
	
	void apply(Canvas imageCanvas);
	
	void cancel();
	
	boolean isInEditMode();
}