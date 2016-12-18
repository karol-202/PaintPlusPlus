package pl.karol202.paintplus.tool.selection;

import pl.karol202.paintplus.R;

public enum ToolSelectionMode
{
	NEW(R.string.selection_mode_new, R.drawable.ic_selection_mode_new_black_24dp),
	ADD(R.string.selection_mode_add, R.drawable.ic_selection_mode_add_black_24dp),
	SUBTRACT(R.string.selection_mode_subtract, R.drawable.ic_selection_mode_subtract_black_24dp),
	MULTIPLY(R.string.selection_mode_multiply, R.drawable.ic_selection_mode_multiply_black_24dp);
	
	private int name;
	private int icon;
	
	ToolSelectionMode(int name, int icon)
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