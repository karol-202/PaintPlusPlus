package pl.karol202.paintplus.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import pl.karol202.paintplus.R;

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
	private int value;
	
	private SeekBar seekBar;
	private TextView textView;
	
	public SeekBarPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_seekbar_preference);
		setPositiveButtonText(R.string.ok);
		setNegativeButtonText(R.string.cancel);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		if(restorePersistedValue) value = getPersistedInt(0);
		else
		{
			value = (Integer) defaultValue;
			persistInt(value);
		}
		update();
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, 0);
	}
	
	@Override
	protected void onBindDialogView(View view)
	{
		super.onBindDialogView(view);
		seekBar = (SeekBar) view.findViewById(R.id.seekBar_preference);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setProgress(value);
		
		textView = (TextView) view.findViewById(R.id.text_seekBar_preference);
		update();
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		if(positiveResult)
		{
			value = seekBar.getProgress();
			persistInt(value);
			update();
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if(textView != null) textView.setText(getTextFromValue(progress));
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) { }
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) { }
	
	private String getTextFromValue(int value)
	{
		return value + "%";
	}
	
	private void update()
	{
		if(textView != null) textView.setText(getTextFromValue(value));
		setSummary(getTextFromValue(value));
	}
}