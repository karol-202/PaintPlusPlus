package pl.karol202.paintplus.image;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class LayersLayoutManager extends LinearLayoutManager
{
	private boolean allowScrolling;
	
	public LayersLayoutManager(Context context)
	{
		super(context);
		allowScrolling = true;
	}
	
	@Override
	public boolean canScrollVertically()
	{
		return allowScrolling;
	}
	
	public void setAllowScrolling(boolean allowScrolling)
	{
		this.allowScrolling = allowScrolling;
	}
}