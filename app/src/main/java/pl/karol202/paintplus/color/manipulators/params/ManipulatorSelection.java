package pl.karol202.paintplus.color.manipulators.params;

import android.graphics.Rect;
import pl.karol202.paintplus.tool.selection.Selection;

public class ManipulatorSelection
{
	private byte[] data;
	private Rect bounds;
	
	public ManipulatorSelection(byte[] data, Rect bounds)
	{
		this.data = data;
		this.bounds = bounds;
	}
	
	//Returns array of bytes corresponding to pixels of selection in given bounds.
	//0 - false, 1 - true
	public static ManipulatorSelection fromSelection(Selection selection, Rect layerBounds)
	{
		Rect bounds = selection.getBounds();
		if(!bounds.intersect(layerBounds)) return null;
		
		byte[] array = new byte[bounds.width() * bounds.height()];
		for(int x = bounds.left; x < bounds.right; x++)
			for(int y = bounds.top; y < bounds.bottom; y++)
			{
				int arrayX = x - bounds.left;
				int arrayY = y - bounds.top;
				array[arrayY * bounds.width() + arrayX] = (byte) (selection.containsPoint(x, y) ? 1 : 0);
			}
		
		return new ManipulatorSelection(array, bounds);
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
	
	public Rect getBounds()
	{
		return bounds;
	}
	
	public void setBounds(Rect bounds)
	{
		this.bounds = bounds;
	}
}