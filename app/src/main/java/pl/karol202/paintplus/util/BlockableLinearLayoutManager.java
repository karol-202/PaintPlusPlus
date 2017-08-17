package pl.karol202.paintplus.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class BlockableLinearLayoutManager extends LinearLayoutManager
{
	private boolean allowScrolling;
	
	public BlockableLinearLayoutManager(Context context)
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