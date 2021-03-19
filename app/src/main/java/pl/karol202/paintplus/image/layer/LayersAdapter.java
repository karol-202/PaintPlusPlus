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

package pl.karol202.paintplus.image.layer;

import android.content.Context;
import android.graphics.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.history.action.ActionLayerDuplicate;
import pl.karol202.paintplus.history.action.ActionLayerJoin;
import pl.karol202.paintplus.history.action.ActionLayerOrderMove;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.options.LegacyOption;

import java.util.ArrayList;
import java.util.HashMap;

import static android.graphics.Bitmap.Config.ARGB_8888;

// TODO Rewrite this class
// Current implementation is flawed, because it uses mutable layer list from image, which may change concurrently
public class LayersAdapter extends RecyclerView.Adapter<LayerViewHolder>
{
	private final String DUPLICATE_INDICATOR;

	private LegacyOption.AppContextLegacy appContext;
	private Image image;
	private ArrayList<Layer> layers;

	private HashMap<Integer, LayerViewHolder> viewHolders;
	private LayerHandle layerHandle;

	public LayersAdapter(ActivityPaint activity)
	{
		this.appContext = activity;
		this.viewHolders = new HashMap<>();
		this.layerHandle = new LayerHandle(activity, this);

		DUPLICATE_INDICATOR = appContext.getContext().getString(R.string.duplicate);
	}

	@NonNull
	@Override
	public LayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(appContext.getContext()).inflate(R.layout.item_layer, parent, false);
		return new LayerViewHolder(this, view);
	}

	@Override
	public void onBindViewHolder(@NonNull LayerViewHolder holder, int position)
	{
		holder.bind(layers.get(position));
		viewHolders.put(position, holder);
	}

	@Override
	public int getItemCount()
	{
		return layers.size();
	}

	Context getContext()
	{
		return appContext.getContext();
	}

	Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		this.layers = image.getLayers();
		notifyDataSetChanged();
	}

	HashMap<Integer, LayerViewHolder> getViewHolders()
	{
		return viewHolders;
	}

	LayerHandle getLayerHandle()
	{
		return layerHandle;
	}

	boolean isLastLayer(Layer layer)
	{
		return layers.indexOf(layer) == layers.size() - 1;
	}

	boolean isLayerSelected(Layer layer)
	{
		return image.isLayerSelected(layer);
	}

	boolean areLayersLocked()
	{
		return image.areLayersLocked();
	}

	void moveLayer(int layerId, int target)
	{
		Layer selected = image.getSelectedLayer();

		Layer layer = layers.remove(layerId);
		layers.add(target, layer);

		image.selectLayer(layers.indexOf(selected));
		image.updateImage();

		ActionLayerOrderMove action = new ActionLayerOrderMove(image);
		action.setSourceAndDestinationLayerPos(layerId, target);
		action.applyAction();
	}

	void duplicateLayer(Layer layer)
	{
		int layerIndex = layers.indexOf(layer);
		String newName = layer.getName() + DUPLICATE_INDICATOR;
		Layer newLayer = new Layer(layer.getX(), layer.getY(), newName, layer.getWidth(), layer.getHeight(), Color.BLACK);
		Bitmap newBitmap = Bitmap.createBitmap(layer.getBitmap());
		newLayer.setBitmap(newBitmap);
		newLayer.setMode(copyLayerMode(layer.getMode()));
		newLayer.setOpacity(layer.getOpacity());
		if(!image.addLayer(newLayer, layerIndex))
			appContext.createSnackbar(R.string.too_many_layers, Toast.LENGTH_SHORT).show();
		else createDuplicateHistoryAction(newLayer);
	}

	private void createDuplicateHistoryAction(Layer newLayer)
	{
		ActionLayerDuplicate action = new ActionLayerDuplicate(image);
		action.setLayerAfterAdding(newLayer);
		action.applyAction();
	}

	void joinWithNextLayer(Layer firstLayer)
	{
		int firstIndex = layers.indexOf(firstLayer);
		Layer secondLayer = layers.get(firstIndex + 1);

		Rect resultBounds = firstLayer.getBounds();
		resultBounds.union(secondLayer.getBounds());

		Matrix matrix = new Matrix();
		matrix.preTranslate(-resultBounds.left, -resultBounds.top);

		Bitmap resultBitmap = Bitmap.createBitmap(resultBounds.width(), resultBounds.height(), ARGB_8888);
		Canvas resultCanvas = new Canvas(resultBitmap);
		resultBitmap = secondLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix);
		resultBitmap = firstLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, matrix);

		Layer resultLayer = new Layer(resultBounds.left, resultBounds.top, firstLayer.getName(),
									  resultBounds.width(), resultBounds.height(), Color.TRANSPARENT);
		resultLayer.setBitmap(resultBitmap);

		image.deleteLayer(firstLayer);
		image.deleteLayer(secondLayer);
		image.addLayer(resultLayer, firstIndex);
		createJoinHistoryAction(firstLayer, secondLayer, firstIndex);
	}

	private void createJoinHistoryAction(Layer firstLayer, Layer secondLayer, int resultLayerId)
	{
		ActionLayerJoin action = new ActionLayerJoin(image);
		action.setLayers(firstLayer, secondLayer, resultLayerId);
		action.applyAction();
	}

	private LegacyLayerMode copyLayerMode(LegacyLayerMode mode)
	{
		try
		{
			return mode.getClass().newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
