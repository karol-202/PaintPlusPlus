package pl.karol202.paintplus.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;

import java.util.ArrayList;
import java.util.HashMap;

public class LayersAdapter extends RecyclerView.Adapter<LayerViewHolder>
{
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
	
	public Context getContext()
	{
		return context;
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public void setImage(Image image)
	{
		this.image = image;
		this.layers = image.getLayers();
		notifyDataSetChanged();
	}
	
	public HashMap<Integer, LayerViewHolder> getViewHolders()
	{
		return viewHolders;
	}
	
	public LayerHandle getLayerHandle()
	{
		return layerHandle;
	}
}