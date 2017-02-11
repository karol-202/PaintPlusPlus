package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import pl.karol202.paintplus.image.layer.Layer;

public interface LayerMode
{
	Bitmap drawLayer(Bitmap bitmap, Matrix matrix);
	
	Bitmap drawLayerAndTool(Bitmap bitmap, Matrix matrix, Bitmap toolBitmap);
	
	Bitmap drawTool(Bitmap bitmap, Bitmap toolBitmap);
	
	void setLayer(Layer layer);
}