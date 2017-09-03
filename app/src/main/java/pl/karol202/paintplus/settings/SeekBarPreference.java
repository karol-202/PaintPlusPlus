package pl.karol202.paintplus.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import pl.karol202.paintplus.R;

public class SeekBarPreference extends DialogPreference
{
	private int value;
	
	public SeekBarPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_seekbar_preference);
		setPositiveButtonText(R.string.ok);
		setNegativeButtonText(R.string.cancel);
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInt(index, 0);
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
	{
		setValue(restorePersistedValue ? getPersistedInt(value) : (int) defaultValue);
		setSummary(SeekBarPreferenceDialogFragmentCompat.getTextFromValue(value));
	}
	
	int getValue()
	{
		return value;
	}
	
	void setValue(int value)
	{
		this.value = value;
		persistInt(value);
	}
}