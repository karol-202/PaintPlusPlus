package pl.karol202.paintplus.color.picker.numerical;

class ColorChannel
{
	private int name;
	private int maxValue;
	private int seekBarColorId;
	
	private boolean active;
	private int value;
	
	ColorChannel(int name, int maxValue, int seekBarColorId)
	{
		this(name, maxValue, seekBarColorId, true);
	}
	
	ColorChannel(int name, int maxValue, int seekBarColorId, boolean active)
	{
		this.name = name;
		this.maxValue = maxValue;
		this.seekBarColorId = seekBarColorId;
		
		this.active = active;
	}
	
	int getName()
	{
		return name;
	}
	
	int getMaxValue()
	{
		return maxValue;
	}
	
	int getSeekBarColorId()
	{
		return seekBarColorId;
	}
	
	boolean isActive()
	{
		return active;
	}
	
	int getValue()
	{
		return value;
	}
	
	void setValue(int value)
	{
		this.value = value;
	}
}