package pl.karol202.paintplus.tool;

import android.app.Fragment;
import pl.karol202.paintplus.tool.properties.PropertiesBrush;
import pl.karol202.paintplus.tool.properties.PropertiesMarker;
import pl.karol202.paintplus.R;

public enum ToolType
{
	MARKER(R.string.tool_marker, R.drawable.ic_marker_black_48dp, ToolMarker.class, PropertiesMarker.class),
	BRUSH(R.string.tool_brush, R.drawable.ic_brush_black_48dp, ToolBrush.class, PropertiesBrush.class);

	private int name;
	private int icon;
	private Class<? extends Tool> toolClass;
	private Class<? extends Fragment> fragmentClass;

	ToolType(int name, int icon, Class<? extends Tool> toolClass, Class<? extends Fragment> fragmentClass)
	{
		this.name = name;
		this.icon = icon;
		this.toolClass = toolClass;
		this.fragmentClass = fragmentClass;
	}

	public int getName()
	{
		return name;
	}

	public int getIcon()
	{
		return icon;
	}

	public Class<? extends Tool> getToolClass()
	{
		return toolClass;
	}

	public Class<? extends Fragment> getFragmentClass()
	{
		return fragmentClass;
	}
}
