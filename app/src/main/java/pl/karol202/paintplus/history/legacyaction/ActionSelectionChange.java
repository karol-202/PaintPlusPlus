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

package pl.karol202.paintplus.history.legacyaction;

import android.graphics.*;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;

public class ActionSelectionChange extends LegacyAction
{
	private static final float SELECTION_LINE_WIDTH = 2f;

	private Region region;

	private Paint selectionPaint;

	public ActionSelectionChange(LegacyImage image)
	{
		super(image);
		createSelectionPaint();
	}

	private void createSelectionPaint()
	{
		selectionPaint = new Paint();
		selectionPaint.setColor(Color.DKGRAY);
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setStrokeWidth(SELECTION_LINE_WIDTH);
	}

	private void showRegionOnBitmap(LegacyImage image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
		getPreviewCanvas().drawPath(transformSelectionPath(image, region), selectionPaint);
	}

	private RectF transformImageRect(LegacyImage image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}

	private Path transformSelectionPath(LegacyImage image, Region region)
	{
		RectF rect = transformImageRect(image);
		Matrix matrix = new Matrix();
		matrix.postScale(rect.width() / image.getWidth(), rect.height() / image.getHeight());
		matrix.postTranslate(rect.left, rect.top);

		Path path = region.getBoundaryPath();
		path.transform(matrix);
		return path;
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image) || region == null) return false;
		LegacySelection selection = image.getSelection();
		Region newRegion = selection.getRegion();
		selection.setRegion(region);
		region = newRegion;

		showRegionOnBitmap(image);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image) || region == null) return false;
		LegacySelection selection = image.getSelection();
		Region oldRegion = selection.getRegion();
		selection.setRegion(region);
		region = oldRegion;

		showRegionOnBitmap(image);
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		Region newRegion = getImage().getSelection().getRegion();
		return region != null && !region.equals(newRegion);
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_selection_change;
	}

	public void setOldRegion()
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.region = new Region(getImage().getSelection().getRegion());
		showRegionOnBitmap(getImage());
	}
}
