package pl.karol202.paintplus.image;

import android.graphics.PointF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import pl.karol202.paintplus.activity.ActivityPaint;

import java.util.HashMap;

public class LayerHandle
{
	private ActivityPaint activity;
	private ViewGroup mainContainer;
	private LayersAdapter adapter;
	private HashMap<Integer, LayerViewHolder> viewHolders;
	private int viewHeight;
	
	private Layer layer;
	private int layerId;
	private LayerViewHolder viewHolder;
	private float oldTouchX;
	private float oldTouchY;
	private float oldOffsetX;
	private float oldOffsetY;
	
	public LayerHandle(ActivityPaint activity, LayersAdapter adapter)
	{
		this.activity = activity;
		this.mainContainer = activity.getMainContainer();
		this.adapter = adapter;
		this.viewHolders = adapter.getViewHolders();
		this.viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LayerViewHolder.HEIGHT_DP, activity.getDisplayMetrics());
	}
	
	public void onTouchStart(float x, float y)
	{
		if(layer == null) return;
		View view = viewHolder.getView();
		PointF originalViewPos = findPointInMainContainer(view, null);
		
		ViewGroup parent = (ViewGroup) view.getParent();
		parent.removeView(view);
		
		viewHolder.bind(layer, true);
		viewHolder.setViewOffset(originalViewPos.x, originalViewPos.y, false);
		mainContainer.addView(view);
		activity.setLayersBlocked(true);
			
		oldOffsetX = originalViewPos.x;
		oldOffsetY = originalViewPos.y;

		oldTouchX = x;
		oldTouchY = y;
	}
	
	private PointF findPointInMainContainer(View view, PointF point)
	{
		if(view == mainContainer) return point;
		if(point == null) point = new PointF();
		point.offset(view.getX(), view.getY());
		
		ViewParent parent = view.getParent();
		if(!(parent instanceof View)) throw new RuntimeException("Unexcpected end of hierarchy.");
		View parentView = (View) parent;
		return findPointInMainContainer(parentView, point);
	}
	
	public void onTouchMove(float x, float y)
	{
		if(layer == null) return;
		
		float deltaX = Math.round(x - oldTouchX);
		float deltaY = Math.round(y - oldTouchY);
		viewHolder.setViewOffset(oldOffsetX + deltaX, oldOffsetY + deltaY, false);
		moveOtherLayers(getCurrentPosition(y));
	}
	
	public void onTouchStop(float x, float y)
	{
		if(layer == null) return;
		
		
		activity.setLayersBlocked(false);
		
		layer = null;
		viewHolder = null;
	}
	
	private int getCurrentPosition(float y)
	{
		float deltaY = Math.round(y - oldTouchY);
		int deltaPos = Math.round(deltaY / viewHeight);
		int current = layerId + deltaPos;
		current = Math.max(current, 0);
		current = Math.min(current, viewHolders.size() - 1);
		return current;
	}
	
	private void moveOtherLayers(int currentPos)
	{
		int one = currentPos > layerId ? 1 : -1;
		Integer firstIndex = layerId + one;
		Integer lastIndex = currentPos;
		
		if(firstIndex > lastIndex)
		{
			int temp = firstIndex;
			firstIndex = lastIndex;
			lastIndex = temp;
		}
		
		for(Integer key : viewHolders.keySet())
		{
			LayerViewHolder holder = viewHolders.get(key);
			boolean visible = key != layerId;
			if(!visible) holder.hide();
			
			boolean inRange = key >= firstIndex && key <= lastIndex;
			if(inRange && currentPos != layerId) holder.setViewOffset(0, -one * viewHeight, true);
			else holder.setViewOffset(0, 0, true);
		}
	}
	
	public void setViewHolder(LayerViewHolder viewHolder)
	{
		this.layer = viewHolder.getLayer();
		this.viewHolder = viewHolder;
		layerId = findViewHolderId(viewHolder);
	}
	
	private int findViewHolderId(LayerViewHolder holder)
	{
		for(Integer key : viewHolders.keySet())
		    if(viewHolders.get(key) == holder) return key;
		throw new NullPointerException("There is no such view holder in holders list.");
	}
}