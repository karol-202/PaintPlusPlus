/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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