package pl.karol202.paintplus.tool.properties;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.tool.Tool;
import pl.karol202.paintplus.tool.Tools;

public abstract class ToolProperties extends Fragment
{
	private int toolId;
	private Tools tools;
	protected Tool tool;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		toolId = bundle.getInt("tool");
		if(toolId == -1) throw new RuntimeException("-1 is not valid key id.");
		return null;
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if(!(context instanceof ActivityPaint))
			throw new RuntimeException("This fragment can only be attached to ActivityPaint.");
		ActivityPaint activity = (ActivityPaint) context;
		tools = activity.getTools();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.tool = tools.getTool(toolId);
	}
}