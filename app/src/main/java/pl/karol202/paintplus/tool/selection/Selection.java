package pl.karol202.paintplus.tool.selection;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import pl.karol202.paintplus.history.action.ActionSelectionChange;
import pl.karol202.paintplus.image.Image;

import java.util.ArrayList;

import static android.graphics.Path.Direction.CW;

public class Selection
{
	public interface OnSelectionChangeListener
	{
		void onSelectionChanged();
	}
	
	private ArrayList<OnSelectionChangeListener> listeners;
	private Image image;
	private Rect imageRect;
	private Region region;
	private Path path;
	private boolean empty;
	
	public Selection(Image image)
	{
		this.listeners = new ArrayList<>();
		this.image = image;
	}
	
	public void init(int width, int height)
	{
		this.imageRect = new Rect(0, 0, width, height);
		this.region = new Region();
		
		updatePath();
	}
	
	public void selectAll()
	{
		commitSelectionRectangle(imageRect, Op.REPLACE);
	}
	
	public void selectNothing()
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();
		
		region.setEmpty();
		updatePath();
		
		action.applyAction();
	}
	
	public void revert()
	{
		commitSelectionRectangle(imageRect, Op.XOR);
	}
	
	void commitSelectionRectangle(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();
		
		region.op(rect, op);
		updatePath();
		
		action.applyAction();
	}
	
	void commitSelectionOval(Rect rect, Op op)
	{
		ActionSelectionChange action = new ActionSelectionChange(image);
		action.setOldRegion();
		
		RectF rectF = new RectF(rect);
		Path ovalPath = new Path();
		ovalPath.addOval(rectF, CW);
		
		Region ovalRegion = new Region();
		ovalRegion.setPath(ovalPath, new Region(0, 0, imageRect.right, imageRect.bottom));
		
		region.op(ovalRegion, op);
		updatePath();
		
		action.applyAction();
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
	
	public Image getImage()
	{
		return image;
	}
	
	public Rect getBounds()
	{
		return region.getBounds();
	}
	
	public Region getRegion()
	{
		return region;
	}
	
	public void setRegion(Region region)
	{
		this.region = region;
		updatePath();
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public void addListener(OnSelectionChangeListener listener)
	{
		if(listeners.contains(listener)) return;
		listeners.add(listener);
	}
}