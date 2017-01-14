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
	private LayerViewHolder originalViewHolder;
	private LayerViewHolder newViewHolder;
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
		if(newViewHolder == null)
		{
			View originalView = originalViewHolder.getView();
			originalView.setVisibility(View.INVISIBLE);
			PointF originalViewPos = findPointInMainContainer(originalView, new PointF(0, 0));
			
			newViewHolder = adapter.createViewHolder();
			newViewHolder.bind(layer, true);
			newViewHolder.setViewOffset(originalViewPos.x, originalViewPos.y);
			mainContainer.addView(newViewHolder.getView());
			activity.setLayersBlocked(true);
			
			oldOffsetX = originalViewPos.x;
			oldOffsetY = originalViewPos.y;
		}
		else
		{
			oldTouchX = x;
			oldTouchY = y;
		}
	}
	
	private PointF findPointInMainContainer(View view, PointF point)
	{
		point = new PointF(point.x, point.y);
		ViewParent parent = view.getParent();
		if(!(parent instanceof View)) throw new RuntimeException("Unexcpected end of hierarchy.");
		View parentView = (View) parent;
		point.offset(parentView.getX(), parentView.getY());
		if(parentView == mainContainer) return point;
		else return findPointInMainContainer(parentView, point);
	}
	
	public void onTouchMove(float x, float y)
	{
		if(layer == null) return;
		
		if(newViewHolder != null)
		{
			float deltaX = Math.round(x - oldTouchX);
			float deltaY = Math.round(y - oldTouchY);
			newViewHolder.setViewOffset(oldOffsetX + deltaX, oldOffsetY + deltaY);
			//System.out.println("DX: " + deltaX + "   DY: " + deltaY + "   X: " + x + "   Y: " + y);
			moveOtherLayers(getCurrentPosition(y));
		}
	}
	
	public void onTouchStop(float x, float y)
	{
		if(layer == null) return;
		
		////Tymczasowo
		//originalViewHolder.getView().setVisibility(View.VISIBLE);
		//mainContainer.removeView(newViewHolder.getView());
		
		activity.setLayersBlocked(false);
		
		layer = null;
		originalViewHolder = null;
		newViewHolder = null;
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
		System.out.println(currentPos);
		int one = currentPos > layerId ? 1 : -1;
		int firstIndex = layerId + one;
		int lastIndex = currentPos;
		for(Integer key : viewHolders.keySet())
		{
			LayerViewHolder holder = viewHolders.get(key);
			if(key >= firstIndex && key <= lastIndex) holder.setViewOffset(0, -one * viewHeight);
			else holder.setViewOffset(0, 0);
		}
	}
	
	public void setViewHolder(LayerViewHolder viewHolder)
	{
		this.layer = viewHolder.getLayer();
				
		if(!viewHolder.isGhost())
		{
			originalViewHolder = viewHolder;
			layerId = findViewHolderId(viewHolder);
		}
		else newViewHolder = viewHolder;
	}
	
	private int findViewHolderId(LayerViewHolder holder)
	{
		for(Integer key : viewHolders.keySet())
		    if(viewHolders.get(key) == holder) return key;
		throw new NullPointerException("There is no such view holder in holders list.");
	}
}