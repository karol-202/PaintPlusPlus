package pl.karol202.paintplus.tool.pickcolor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.tool.ToolProperties;

public class ColorPickProperties extends ToolProperties
{
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.properties_color_pick, container, false);
		return view;
	}
}