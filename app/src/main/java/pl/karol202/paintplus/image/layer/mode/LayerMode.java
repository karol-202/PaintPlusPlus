package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import pl.karol202.paintplus.image.layer.Layer;

public interface LayerMode
{
	Bitmap drawLayer(Bitmap bitmap, Layer layer, Matrix matrix);
	
	void setAntialiasing(boolean antialiasing);
	
	int getName();
}