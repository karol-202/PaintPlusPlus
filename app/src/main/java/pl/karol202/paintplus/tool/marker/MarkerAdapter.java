package pl.karol202.paintplus.tool.marker;

import android.graphics.Canvas;

interface MarkerAdapter
{
	void onBeginDraw(float x, float y);
	
	void onDraw(float x, float y);
	
	void onEndDraw(float x, float y);
	
	void onScreenDraw(Canvas canvas);
}