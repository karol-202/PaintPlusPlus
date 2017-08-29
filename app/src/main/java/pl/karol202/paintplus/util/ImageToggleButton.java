package pl.karol202.paintplus.util;

import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import pl.karol202.paintplus.R;

public class ImageToggleButton extends AppCompatImageButton
{
	public interface OnCheckedChangeListener
	{
		void onCheckedChanged(ImageToggleButton button, boolean checked);
	}
	
	private static final int[] STATE_CHECKED = { R.attr.checked };
	
	private OnCheckedChangeListener listener;
	private boolean checked;
	
	public ImageToggleButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	public boolean performClick()
	{
		checked = !checked;
		if(listener != null) listener.onCheckedChanged(this, checked);
		return super.performClick();
	}
	
	@Override
	public int[] onCreateDrawableState(int extraSpace)
	{
		int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if(checked) mergeDrawableStates(drawableState, STATE_CHECKED);
		return drawableState;
	}
	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener)
	{
		this.listener = listener;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public void setChecked(boolean checked)
	{
		this.checked = checked;
		refreshDrawableState();
	}
}