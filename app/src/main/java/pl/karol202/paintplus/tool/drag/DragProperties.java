package pl.karol202.paintplus.tool.drag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

public class DragProperties extends ToolProperties
{
	private ToolDrag drag;
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.properties_drag, container, false);
		drag = (ToolDrag) tool;
		
		return view;
	}
}