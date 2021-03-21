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

package pl.karol202.paintplus.image;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import pl.karol202.paintplus.history.action.ActionSelectionChange;
import pl.karol202.paintplus.image.LegacyImage;

import java.util.ArrayList;

import static android.graphics.Path.Direction.CW;

public class LegacySelection
{
	public interface OnSelectionChangeListener
	{
		void onSelectionChanged();
	}

	private ArrayList<OnSelectionChangeListener> listeners;
	private LegacyImage image;
	private Rect imageRect;
	private Region region;
	private Path path;
	private boolean empty;

	public LegacySelection(LegacyImage image)
	{
		this.listeners = new ArrayList<>();
		this.image = image;
	}

	public void init(int width, int height)
	{
		this.imageRect = new Rect(0, 0, width, height);
		this.region = new Region();

		updatePath();
	}

	public void selectAll()
	{
		commitSelectionRectangle(imageRect, Op.REPLACE);
	}

	public void selectNothing()
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();

		region.setEmpty();
		updatePath();

		action.applyAction();
	}

	public void revert()
	{
		commitSelectionRectangle(imageRect, Op.XOR);
	}

	public void commitSelectionRectangle(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();

		region.op(rect, op);
		updatePath();

		action.applyAction();
	}

	public void commitSelectionOval(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();

		RectF rectF = new RectF(rect);
		Path ovalPath = new Path();
		ovalPath.addOval(rectF, CW);

		Region ovalRegion = new Region();
		ovalRegion.setPath(ovalPath, new Region(0, 0, imageRect.right, imageRect.bottom));

		region.op(ovalRegion, op);
		updatePath();

		action.applyAction();
	}

	public void offsetSelection(int x, int y)
	{
		region.translate(x, y);
		updatePath();
	}

	private void updatePath()
	{
		path = region.getBoundaryPath();

		empty = region.isEmpty();
		for(OnSelectionChangeListener listener : listeners) listener.onSelectionChanged();
	}

	public boolean isEmpty()
	{
		return empty;
	}

	public boolean containsPoint(int x, int y)
	{
		return region.contains(x, y);
	}

	public LegacyImage getImage()
	{
		return image;
	}

	public Rect getBounds()
	{
		return region.getBounds();
	}

	public Region getRegion()
	{
		return region;
	}

	public void setRegion(Region region)
	{
		this.region = region;
		updatePath();
	}

	public Path getPath()
	{
		return path;
	}

	public void addListener(OnSelectionChangeListener listener)
	{
		if(listeners.contains(listener)) return;
		listeners.add(listener);
	}
}
