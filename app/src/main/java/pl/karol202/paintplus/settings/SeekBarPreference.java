/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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