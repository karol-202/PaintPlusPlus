package pl.karol202.paintplus.image.layer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import pl.karol202.paintplus.util.ItemDivider;
import pl.karol202.paintplus.util.BlockableLinearLayoutManager;

public class LayersRecyclerView extends RecyclerView
{
	private int maxHeight;
	private BlockableLinearLayoutManager manager;
	
	public LayersRecyclerView(Context context)
	{
		this(context, null);
	}
	
	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		this(context, attrs, 0);
	}
	
	public LayersRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		manager = new BlockableLinearLayoutManager(context);
		setLayoutManager(manager);
		addItemDecoration(new ItemDivider(context));
	}
	
	@Override
	protected void onMeasure(int widthSpec, int heightSpec)
	{
		super.onMeasure(widthSpec, heightSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		if(height > maxHeight && maxHeight != 0) height = maxHeight;
		setMeasuredDimension(width, height);
	}
	
	public void setMaxHeight(int maxHeight)
	{
		if(maxHeight >= 0) this.maxHeight = maxHeight;
	}
	
	public void setAllowScrolling(boolean allowScrolling)
	{
		manager.setAllowScrolling(allowScrolling);
	}
}