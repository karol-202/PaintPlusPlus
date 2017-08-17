package pl.karol202.paintplus.color;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class ColorsSet implements Parcelable
{
	public interface OnColorsChangeListener
	{
		void onColorsChanged();
	}
	
	private int firstColor;
	private int secondColor;
	private OnColorsChangeListener listener;

	public ColorsSet(int firstColor, int secondColor)
	{
		this.firstColor = firstColor;
		this.secondColor = secondColor;
	}

	void revert()
	{
		int first = firstColor;
		firstColor = secondColor;
		secondColor = first;
		listener.onColorsChanged();
	}

	public int getFirstColor()
	{
		return firstColor;
	}

	public void setFirstColor(int firstColor)
	{
		this.firstColor = firstColor;
		listener.onColorsChanged();
	}

	public int getSecondColor()
	{
		return secondColor;
	}

	public void setSecondColor(int secondColor)
	{
		this.secondColor = secondColor;
		listener.onColorsChanged();
	}
	
	public void setListener(OnColorsChangeListener listener)
	{
		this.listener = listener;
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
