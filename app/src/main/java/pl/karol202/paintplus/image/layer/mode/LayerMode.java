package pl.karol202.paintplus.image.layer.mode;

import android.graphics.Canvas;
import android.graphics.Matrix;
import pl.karol202.paintplus.image.layer.Layer;

public interface LayerMode
{
	void drawLayer(Canvas canvas, Layer layer, Matrix matrix);
	
	void setAntialiasing(boolean antialiasing);
	
	int getName();
}