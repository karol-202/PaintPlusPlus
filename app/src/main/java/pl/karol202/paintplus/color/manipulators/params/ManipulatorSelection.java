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

package pl.karol202.paintplus.color.manipulators.params;

import android.graphics.Rect;
import pl.karol202.paintplus.image.LegacySelection;

public class ManipulatorSelection
{
	private byte[] data;
	private Rect bounds;

	public ManipulatorSelection(byte[] data, Rect bounds)
	{
		this.data = data;
		this.bounds = bounds;
	}

	// Creates array of bytes corresponding to pixels of selection in given bounds.
	// 0 - false, 1 - true
	public static ManipulatorSelection fromSelection(LegacySelection selection, Rect layerBounds)
	{
		Rect bounds = selection.getBounds();
		if(bounds.isEmpty() || !bounds.intersect(layerBounds)) return null;

		byte[] array = new byte[bounds.width() * bounds.height()];
		for(int x = bounds.left; x < bounds.right; x++)
			for(int y = bounds.top; y < bounds.bottom; y++)
			{
				int arrayX = x - bounds.left;
				int arrayY = y - bounds.top;
				array[arrayY * bounds.width() + arrayX] = (byte) (selection.containsPoint(x, y) ? 1 : 0);
			}

		bounds.offset(-layerBounds.left, -layerBounds.top);
		return new ManipulatorSelection(array, bounds);
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	public Rect getBounds()
	{
		return bounds;
	}
}
