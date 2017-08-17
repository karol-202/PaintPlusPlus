package pl.karol202.paintplus.image.layer;

import android.animation.Animator;
import android.graphics.PointF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import pl.karol202.paintplus.activity.ActivityPaint;

import java.util.HashMap;

class LayerHandle implements Animator.AnimatorListener
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
	private int currentPos;
	
	LayerHandle(ActivityPaint activity, LayersAdapter adapter)
	{
		this.activity = activity;
		this.mainContainer = activity.getMainContainer();
		this.adapter = adapter;
		this.viewHolders = adapter.getViewHolders();
		this.viewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LayerViewHolder.HEIGHT_DP, activity.getDisplayMetrics());
	}
	
	void onTouchStart(float x, float y)
	{
		if(layer == null) return;
		View view = viewHolder.getView();
		PointF originalViewPos = findPointInMainContainer(view, null);
		
		ViewGroup parent = (ViewGroup) view.getParent();
		parent.removeView(view);
		
		viewHolder.setGhost();
		viewHolder.setViewOffset(originalViewPos.x, originalViewPos.y);
		mainContainer.addView(view);
		activity.setScrollingBlocked(true);
			
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
	
	void onTouchMove(float x, float y)
	{
		if(layer == null) return;
		
		float deltaX = Math.round(x - oldTouchX);
		float deltaY = Math.round(y - oldTouchY);
		viewHolder.setViewOffset(oldOffsetX + deltaX, oldOffsetY + deltaY);
		moveOtherLayers(getCurrentPosition(y));
	}
	
	void onTouchStop(float x, float y)
	{
		if(layer == null) return;
		
		currentPos = getCurrentPosition(y);
		float targetGhostPos = oldOffsetY + ((currentPos - layerId) * viewHeight);
		viewHolder.setViewOffsetWithAnimation(0, targetGhostPos, this);
		moveOtherLayers(currentPos);
		
		activity.setScrollingBlocked(false);
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
		
		LayerViewHolder holderReplacement = viewHolders.get(layerId);
		holderReplacement.hide();
		
		for(Integer key : viewHolders.keySet())
		{
			LayerViewHolder holder = viewHolders.get(key);
			
			boolean inRange = key >= firstIndex && key <= lastIndex;
			if(inRange && currentPos != layerId) holder.setViewOffsetWithAnimation(0, -one * viewHeight, null);
			else holder.setViewOffsetWithAnimation(0, 0, null);
		}
	}
	
	@Override
	public void onAnimationStart(Animator animation) { }
	
	@Override
	public void onAnimationEnd(Animator animation)
	{
		dropLayer();
	}
	
	@Override
	public void onAnimationCancel(Animator animation) { }
	
	@Override
	public void onAnimationRepeat(Animator animation) { }
	
	private void dropLayer()
	{
		mainContainer.removeView(viewHolder.getView());
		adapter.moveLayer(layerId, currentPos);
		adapter.notifyDataSetChanged();
		
		layer = null;
		viewHolder = null;
	}
	
	void setViewHolder(LayerViewHolder viewHolder)
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