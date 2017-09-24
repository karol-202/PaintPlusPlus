package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import pl.karol202.paintplus.image.layer.Layer;

public interface LayerMode
{
	void startDrawing(Bitmap bitmapDst, Canvas canvasDst);
	
	void addLayer(Matrix matrixLayer);
	
	void addTool(Bitmap bitmapTool);
	
	void setRectClipping(RectF clipRect);
	
	void resetClipping();
	
	Bitmap apply();
	
	void setLayer(Layer layer);
	
	boolean replacesBitmap();
}