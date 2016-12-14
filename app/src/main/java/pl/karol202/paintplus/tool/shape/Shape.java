package pl.karol202.paintplus.tool.shape;

import android.graphics.Canvas;
import android.view.MotionEvent;
import pl.karol202.paintplus.color.ColorsSet;
import pl.karol202.paintplus.tool.shape.properties.ShapeProperties;

public interface Shape
{
	int getName();
	
	int getIcon();
	
	Class<? extends ShapeProperties> getPropertiesClass();
	
	boolean onTouch(MotionEvent event);
	
	void onScreenDraw(Canvas canvas, ColorsSet colors);
	
	void apply(Canvas imageCanvas, ColorsSet colors);
	
	void cancel();
	
	boolean isInEditMode();
}