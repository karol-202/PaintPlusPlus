package pl.karol202.paintplus.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

public interface Tool
{
	int getName();
	
	int getIcon();
	
	Class<? extends ToolProperties> getPropertiesFragmentClass();
	
	CoordinateSpace getCoordinateSpace();
	
	boolean isUsingSnapping();
	
	boolean onTouch(MotionEvent event, Context context);
	
	boolean isImageLimited();
	
	boolean doesScreenDraw(boolean layerVisible);
	
	boolean isDrawingOnTop();

	void onScreenDraw(Canvas canvas);
}