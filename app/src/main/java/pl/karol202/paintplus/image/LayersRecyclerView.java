package pl.karol202.paintplus.image;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class LayersRecyclerView extends RecyclerView
{
	private int maxHeight;
	
	public LayersRecyclerView(Context context)
	{
		super(context);
	}
	
	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{
		super.onMeasure(widthSpec, heightSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		if(height > maxHeight) height = maxHeight;
		setMeasuredDimension(width, height);
	}
	
	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight = maxHeight;
	}
}