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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.history.action.ActionLayerDuplicate;
import pl.karol202.paintplus.history.action.ActionLayerJoin;
import pl.karol202.paintplus.history.action.ActionLayerOrderMove;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.mode.LayerMode;

import java.util.ArrayList;
import java.util.HashMap;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class LayersAdapter extends RecyclerView.Adapter<LayerViewHolder>
{
	private final String DUPLICATE_INDICATOR;
	
	private Context context;
	private Image image;
	private ArrayList<Layer> layers;
	
	private HashMap<Integer, LayerViewHolder> viewHolders;
	private LayerHandle layerHandle;
	
	public LayersAdapter(ActivityPaint activity)
	{
		this.context = activity;
		this.viewHolders = new HashMap<>();
		this.layerHandle = new LayerHandle(activity, this);
		
		DUPLICATE_INDICATOR = context.getString(R.string.duplicate);
	}
	
	@Override
	public LayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.item_layer, parent, false);
		return new LayerViewHolder(this, view);
	}
	
	@Override
	public void onBindViewHolder(LayerViewHolder holder, int position)
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
		return context;
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
		Layer newLayer = new Layer(layer.getX(), layer.getY(), layer.getWidth(), layer.getHeight(), newName, Color.BLACK);
		Bitmap newBitmap = Bitmap.createBitmap(layer.getBitmap());
		newLayer.setBitmap(newBitmap);
		newLayer.setMode(copyLayerMode(layer.getMode()));
		newLayer.setOpacity(layer.getOpacity());
		if(!image.addLayer(newLayer, layerIndex))
			Toast.makeText(context, R.string.too_many_layers, Toast.LENGTH_SHORT).show();
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
		firstLayer.setPosition(firstLayer.getX() - resultBounds.left,
							   firstLayer.getY() - resultBounds.top);
		
		Bitmap resultBitmap = Bitmap.createBitmap(resultBounds.width(), resultBounds.height(), ARGB_8888);
		Canvas resultCanvas = new Canvas(resultBitmap);
		resultCanvas.drawBitmap(secondLayer.getBitmap(), secondLayer.getX() - resultBounds.left,
												         secondLayer.getY() - resultBounds.top, null);
		resultBitmap = firstLayer.drawLayerAndReturnBitmap(resultBitmap, resultCanvas, null, new Matrix());
		
		Layer resultLayer = new Layer(resultBounds.left, resultBounds.top,
									  resultBounds.width(), resultBounds.height(),
									  firstLayer.getName(), Color.TRANSPARENT);
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
	
	private LayerMode copyLayerMode(LayerMode mode)
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