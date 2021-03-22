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

import android.graphics.Bitmap;
import android.graphics.Color;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.image.LegacyImage;
import pl.karol202.paintplus.image.layer.Layer;

public class ActionLayerJoin extends LegacyAction
{
	private interface ActionState
	{
		boolean isComplete();

		void revert(LegacyImage image);

		Bitmap getStatePreview(LegacyImage image);
	}

	private class ActionStateDone implements ActionState
	{
		private Layer topLayer;
		private Layer bottomLayer;
		private int resultLayerId;

		ActionStateDone(Layer topLayer, Layer bottomLayer, int resultLayerId)
		{
			this.topLayer = topLayer;
			this.bottomLayer = bottomLayer;
			this.resultLayerId = resultLayerId;
		}

		@Override
		public boolean isComplete()
		{
			return topLayer != null && bottomLayer != null && resultLayerId != -1;
		}

		@Override
		public void revert(LegacyImage image)
		{
			image.deleteLayer(image.getLayerAtIndex(resultLayerId));
			image.addLayer(bottomLayer, resultLayerId);
			image.addLayer(topLayer, resultLayerId);
		}

		@Override
		public Bitmap getStatePreview(LegacyImage image)
		{
			return image.getLayerAtIndex(resultLayerId).getBitmap();
		}

		int getResultLayerId()
		{
			return resultLayerId;
		}
	}

	private class ActionStateUndone implements ActionState
	{
		private int topLayerId;
		private int bottomLayerId;
		private Layer resultLayer;

		ActionStateUndone(int topLayerId, int bottomLayerId, Layer resultLayer)
		{
			this.topLayerId = topLayerId;
			this.bottomLayerId = bottomLayerId;
			this.resultLayer = resultLayer;
		}

		@Override
		public boolean isComplete()
		{
			return topLayerId != -1 && bottomLayerId != -1 && resultLayer != null;
		}

		@Override
		public void revert(LegacyImage image)
		{
			Layer topLayer = image.getLayerAtIndex(topLayerId);
			Layer bottomLayer = image.getLayerAtIndex(bottomLayerId);
			image.deleteLayer(topLayer);
			image.deleteLayer(bottomLayer);
			image.addLayer(resultLayer, topLayerId);
		}

		@Override
		public Bitmap getStatePreview(LegacyImage image)
		{
			return resultLayer.getBitmap();
		}

		int getTopLayerId()
		{
			return topLayerId;
		}

		int getBottomLayerId()
		{
			return bottomLayerId;
		}
	}

	private ActionState state;

	public ActionLayerJoin(LegacyImage image)
	{
		super(image);
	}

	private void updateBitmap(LegacyImage image)
	{
		Bitmap layerBitmap = state.getStatePreview(image);
		getPreviewBitmap().eraseColor(Color.TRANSPARENT);
		getPreviewCanvas().drawBitmap(layerBitmap, null, transformLayerRect(layerBitmap), null);
	}

	@Override
	public boolean undo(LegacyImage image)
	{
		if(!super.undo(image) || !(state instanceof ActionStateDone) && !state.isComplete()) return false;
		ActionStateDone stateDone = (ActionStateDone) state;
		int topLayerId = stateDone.getResultLayerId();
		int bottomLayerId = stateDone.getResultLayerId() + 1;
		Layer resultLayer = image.getLayerAtIndex(stateDone.getResultLayerId());

		state.revert(image);
		state = new ActionStateUndone(topLayerId, bottomLayerId, resultLayer);
		return true;
	}

	@Override
	public boolean redo(LegacyImage image)
	{
		if(!super.redo(image) || !(state instanceof ActionStateUndone) && state.isComplete()) return false;
		ActionStateUndone stateUndone = (ActionStateUndone) state;
		Layer topLayer = image.getLayerAtIndex(stateUndone.getTopLayerId());
		Layer bottomLayer = image.getLayerAtIndex(stateUndone.getBottomLayerId());
		int resultLayerId = stateUndone.getTopLayerId();

		state.revert(image);
		state = new ActionStateDone(topLayer, bottomLayer, resultLayerId);
		return true;
	}

	@Override
	boolean canApplyAction()
	{
		return state != null && state.isComplete();
	}

	@Override
	public int getActionName()
	{
		return R.string.history_action_layer_join;
	}

	public void setLayers(Layer topLayer, Layer bottomLayer, int resultLayerId)
	{
		if(isApplied()) throw new IllegalStateException("Cannot alter history.");
		this.state = new ActionStateDone(topLayer, bottomLayer, resultLayerId);
		updateBitmap(getImage());
	}
}
