package pl.karol202.paintplus.color.picker.numerical;

class ColorChannel
{
	private int name;
	private int maxValue;
	
	private boolean active;
	private int value;
	
	ColorChannel(int name, int maxValue, boolean active)
	{
		this.name = name;
		this.maxValue = maxValue;
		
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