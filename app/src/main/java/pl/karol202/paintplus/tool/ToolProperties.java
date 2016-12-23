package pl.karol202.paintplus.tool;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.karol202.paintplus.activity.ActivityPaint;

public abstract class ToolProperties extends Fragment
{
	private int toolId;
	private ActivityPaint activityPaint;
	private Tools tools;
	protected Tool tool;
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		if(!(context instanceof ActivityPaint))
			throw new RuntimeException("This fragment can only be attached to ActivityPaint.");
		activityPaint = (ActivityPaint) context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		tools = activityPaint.getTools();
		Bundle bundle = getArguments();
		if(bundle == null) throw new RuntimeException("No arguments found.");
		this.toolId = bundle.getInt("tool");
		if(toolId == -1) throw new RuntimeException("-1 is not valid tool id.");
		this.tool = tools.getTool(toolId);
		return null;
	}
}