package pl.karol202.paintplus.tool.properties;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public abstract class ToolProperties extends Fragment
{
	protected Tool tool;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		int toolId = bundle.getInt("tool");
		if(toolId == -1) throw new RuntimeException("-1 is not valid key id.");
		this.tool = Tools.getTool(toolId);
		return null;
	}
}