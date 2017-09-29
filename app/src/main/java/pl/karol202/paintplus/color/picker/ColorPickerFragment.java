package pl.karol202.paintplus.color.picker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class ColorPickerFragment extends Fragment
{
	private ActivityColorSelect activityColorSelect;
	private boolean useAlpha;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(getArguments() != null) useAlpha = getArguments().getBoolean("useAlpha");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		init(activity);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		init(context);
	}
	
	private void init(Context context)
	{
		if(!(context instanceof ActivityColorSelect))
			throw new IllegalStateException("ColorPickerFragment must be attached to ActivityColorSelect.");
		activityColorSelect = (ActivityColorSelect) context;
	}
	
	protected abstract boolean onColorModeSelected(int actionId);
	
	protected abstract boolean isColorModeSupported(int actionId);
	
	public boolean isUsingAlpha()
	{
		return useAlpha;
	}
	
	public int getCurrentColor()
	{
		return activityColorSelect.getCurrentColor();
	}
	
	protected void setCurrentColor(int color)
	{
		activityColorSelect.setCurrentColor(color);
	}
}