package pl.karol202.paintplus.tool.selection;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;

public class Selection
{
	private Rect imageRect;
	private Region region;
	private Path path;
	private Path limitedPath;
	private boolean empty;
	
	public Selection(int width, int height)
	{
		this.imageRect = new Rect(0, 0, width, height);
		this.region = new Region();
		this.path = new Path();
		this.limitedPath = new Path();
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
	
	public void commitSelection(Rect rect, Op op)
	{
		region.op(rect, op);
		updatePath();
	}
	
	public boolean isEmpty()
	{
		return empty;
	}
	
	public boolean containsPoint(int x, int y)
	{
		return region.contains(x, y);
	}
	
	private void updatePath()
	{
		path = region.getBoundaryPath();
		
		Region limitedRegion = new Region(region);
		limitedRegion.op(imageRect, Op.INTERSECT);
		limitedPath = limitedRegion.getBoundaryPath();
		
		empty = region.isEmpty();
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public Path getLimitedPath()
	{
		return limitedPath;
	}
}