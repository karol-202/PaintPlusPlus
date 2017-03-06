package pl.karol202.paintplus.tool.selection;

import pl.karol202.paintplus.R;

public enum  ToolSelectionShape
{
	RECTANGLE(R.string.selection_shape_rectangle, R.drawable.ic_selection_rectangular_black_24px),
	OVAL(R.string.selection_shape_oval, R.drawable.ic_selection_circular_black_24dp);
	
	private int name;
	private int icon;
	
	ToolSelectionShape(int name, int icon)
	{
		this.name = name;
		this.icon = icon;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getIcon()
	{
		return icon;
	}
}