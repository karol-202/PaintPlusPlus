package pl.karol202.paintplus.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;

public interface Tool
{
	int getName();
	
	int getIcon();
	
	Class<? extends ToolProperties> getPropertiesFragmentClass();
	
	ToolCoordinateSpace getCoordinateSpace();
	
	boolean isUsingSnapping();
	
	boolean onTouch(MotionEvent event, Context context);
	
	boolean providesDirtyRegion();
	
	Rect getDirtyRegion();
	
	void resetDirtyRegion();
	
	boolean doesOnLayerDraw(boolean layerVisible);
	
	boolean doesOnTopDraw();
	
	ToolCoordinateSpace getOnLayerDrawingCoordinateSpace();
	
	ToolCoordinateSpace getOnTopDrawingCoordinateSpace();

	void onLayerDraw(Canvas canvas);
	
	void onTopDraw(Canvas canvas);
}