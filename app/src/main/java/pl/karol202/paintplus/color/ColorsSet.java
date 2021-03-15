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

package pl.karol202.paintplus.color;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ColorsSet implements Parcelable
{
	public interface OnColorsChangeListener
	{
		void onColorsChanged();
	}

	private int firstColor;
	private int secondColor;
	private List<OnColorsChangeListener> listeners;

	public ColorsSet(int firstColor, int secondColor)
	{
		this.firstColor = firstColor;
		this.secondColor = secondColor;
		this.listeners = new ArrayList<>();
	}

	void invert()
	{
		int first = firstColor;
		firstColor = secondColor;
		secondColor = first;
		for(OnColorsChangeListener listener : listeners) listener.onColorsChanged();
	}

	public int getFirstColor()
	{
		return firstColor;
	}

	public void setFirstColor(int firstColor)
	{
		this.firstColor = firstColor;
		for(OnColorsChangeListener listener : listeners) listener.onColorsChanged();
	}

	public int getSecondColor()
	{
		return secondColor;
	}

	void setSecondColor(int secondColor)
	{
		this.secondColor = secondColor;
		for(OnColorsChangeListener listener : listeners) listener.onColorsChanged();
	}

	void addListener(OnColorsChangeListener listener)
	{
		listeners.add(listener);
	}

	public static final Parcelable.Creator<ColorsSet> CREATOR = new Parcelable.Creator<ColorsSet>()
	{
		@Override
		public ColorsSet createFromParcel(Parcel source)
		{
			return new ColorsSet(source);
		}

		@Override
		public ColorsSet[] newArray(int size)
		{
			return new ColorsSet[size];
		}
	};

	private ColorsSet(Parcel source)
	{
		this(source.readInt(), source.readInt());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(firstColor);
		dest.writeInt(secondColor);
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	public static ColorsSet getDefault()
	{
		return new ColorsSet(Color.BLACK, Color.WHITE);
	}
}
