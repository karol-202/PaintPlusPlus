package pl.karol202.paintplus.color.picker.numerical;

public class ColorChannel
{
	private int name;
	private int maxValue;
	
	private boolean active;
	private int value;
	
	public ColorChannel(int name, int maxValue)
	{
		this(name, maxValue, true);
	}
	
	public ColorChannel(int name, int maxValue, boolean active)
	{
		this.name = name;
		this.maxValue = maxValue;
		
		this.active = active;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getMaxValue()
	{
		return maxValue;
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
}