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

package pl.karol202.paintplus.history.action;

import android.graphics.Color;
import android.graphics.RectF;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.RotationAmount;

public class ActionImageRotate extends Action
{
	private RotationAmount rotationAmount;
	
	public ActionImageRotate(Image image)
	{
		super(image);
	}
	
	private void updateBitmap(Image image)
	{
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(image.getFullImage(), null, transformImageRect(image), null);
	}
	
	private RectF transformImageRect(Image image)
	{
		float max = Math.max(image.getWidth(), image.getHeight());
		float ratio = getPreviewRect().width() / max;
		RectF rect = new RectF(0, 0, image.getWidth() * ratio, image.getHeight() * ratio);
		rect.offset(getPreviewRect().centerX() - rect.centerX(), getPreviewRect().centerY() - rect.centerY());
		return rect;
	}
	
	@Override
	public boolean undo(Image image)
	{
		if(!super.undo(image)) return false;
		updateBitmap(image);
		RotationAmount amount = null;
		if(rotationAmount == RotationAmount.ANGLE_90) amount = RotationAmount.ANGLE_270;
		else if(rotationAmount == RotationAmount.ANGLE_180) amount = RotationAmount.ANGLE_180;
		else if(rotationAmount == RotationAmount.ANGLE_270) amount = RotationAmount.ANGLE_90;
		if(amount != null) image.rotate(amount);
		return true;
	}
	
	@Override
	public boolean redo(Image image)
	{
		if(!super.redo(image)) return false;
		updateBitmap(image);
		image.rotate(rotationAmount);
		return true;
	}
	
	@Override
	boolean canApplyAction()
	{
		return rotationAmount != null;
	}
	
	@Override
	public int getActionName()
	{
		return R.string.history_action_image_rotate;
	}
	
	public void setRotationAmount(RotationAmount rotationAmount)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history!");
		this.rotationAmount = rotationAmount;
		updateBitmap(getImage());
	}
}