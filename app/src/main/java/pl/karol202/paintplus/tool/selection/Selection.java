package pl.karol202.paintplus.tool.selection;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;

import java.util.ArrayList;

public class Selection
{
	public interface OnSelectionChangeListener
	{
		void onSelectionChanged();
	}
	
	private ArrayList<OnSelectionChangeListener> listeners;
	private Rect imageRect;
	private Region region;
	private Path path;
	private boolean empty;
	
	public Selection()
	{
		this.listeners = new ArrayList<>();
	}
	
	public void init(int width, int height)
	{
		this.imageRect = new Rect(0, 0, width, height);
		this.region = new Region();
		this.path = new Path();
		updatePath();
	}
	
	public void selectAll()
	{
		commitSelection(imageRect, Op.REPLACE);
	}
	
	public void selectNothing()
	{
		region.setEmpty();
		updatePath();
	}
	
	public void revert()
	{
		commitSelection(imageRect, Op.XOR);
	}
	
	public void commitSelection(Rect rect, Op op)
	{
		region.op(rect, op);
		updatePath();
	}
	
	private void updatePath()
	{
		path = region.getBoundaryPath();
		
		empty = region.isEmpty();
		for(OnSelectionChangeListener listener : listeners) listener.onSelectionChanged();
	}
	
	public boolean isEmpty()
	{
		return empty;
	}
	
	public boolean containsPoint(int x, int y)
	{
		return region.contains(x, y);
	}
	
	public Rect getBounds()
	{
		return region.getBounds();
	}
	
	public Region getRegion()
	{
		return region;
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public void addListener(OnSelectionChangeListener listener)
	{
		listeners.add(listener);
	}
}