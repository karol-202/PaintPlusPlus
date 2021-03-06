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

import android.graphics.*;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.util.GraphicsHelper;

public abstract class LayerModeRenderscript<S extends LayerScript> implements LayerMode
{
	private Layer layer;
	private Paint paint;
	private RenderScript rs;
	private S script;

	private Bitmap bitmapDst;

	private Bitmap bitmapSrc;
	private Canvas canvasSrc;

	private Bitmap bitmapOut;
	private Allocation allocationOut;

	LayerModeRenderscript()
	{
		paint = new Paint();
		rs = GraphicsHelper.getRenderScript();
		script = getNewScript(rs);
	}

	protected abstract S getNewScript(RenderScript renderScript);

	@Override
	public void startDrawing(Bitmap bitmapDst, Canvas canvasDst)
	{
		if(layer == null) throw new NullPointerException("Layer is null");
		this.bitmapDst = bitmapDst;

		updateSrcIfOutdated();
		canvasSrc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		updateOutIfOutdated();

		paint.setFilterBitmap(LayerModeType.isAntialiasing());
	}

	private void updateSrcIfOutdated()
	{
		if(bitmapSrc != null && bitmapSrc.getWidth() == bitmapDst.getWidth() && bitmapSrc.getHeight() == bitmapDst.getHeight())
			return;
		bitmapSrc = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		canvasSrc = new Canvas(bitmapSrc);
	}

	private void updateOutIfOutdated()
	{
		if(bitmapOut != null && bitmapOut.getWidth() == bitmapDst.getWidth() && bitmapOut.getHeight() == bitmapDst.getHeight())
			return;
		bitmapOut = Bitmap.createBitmap(bitmapDst.getWidth(), bitmapDst.getHeight(), Bitmap.Config.ARGB_8888);
		allocationOut = Allocation.createFromBitmap(rs, bitmapOut);
	}

	@Override
	public void addLayer(Matrix matrixLayer)
	{
		canvasSrc.drawBitmap(layer.getBitmap(), matrixLayer, paint);
	}

	@Override
	public void addTool(Bitmap bitmapTool)
	{
		canvasSrc.drawBitmap(bitmapTool, 0, 0, paint);
	}

	@Override
	public void setRectClipping(RectF clipRect)
	{
		if(canvasSrc.getSaveCount() > 0) canvasSrc.restoreToCount(1);
		canvasSrc.save();
		canvasSrc.clipRect(clipRect);
	}

	@Override
	public void resetClipping()
	{
		canvasSrc.restore();
	}

	@Override
	public Bitmap apply()
	{
		Allocation allocationDst = Allocation.createFromBitmap(rs, bitmapDst);
		Allocation allocationSrc = Allocation.createFromBitmap(rs, bitmapSrc);

		script.setDstAllocation(allocationDst);
		script.setOpacity(layer.getOpacity());
		script.run(allocationSrc, allocationOut);
		allocationOut.copyTo(bitmapOut);

		return bitmapOut;
	}

	@Override
	public void setLayer(Layer layer)
	{
		this.layer = layer;
	}

	@Override
	public boolean replacesBitmap()
	{
		return true;
	}
}
