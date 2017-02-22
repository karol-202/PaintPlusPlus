package pl.karol202.paintplus.color.manipulators.params;

public class BrightnessParams extends ColorsManipulatorParams
{
	private float brightness;
	private float contrast;
	
	public BrightnessParams(ManipulatorSelection selection)
	{
		super(selection);
	}
	
	public float getBrightness()
	{
		return brightness;
	}
	
	public void setBrightness(float brightness)
	{
		this.brightness = brightness;
	}
	
	public float getContrast()
	{
		return contrast;
	}
	
	public void setContrast(float contrast)
	{
		this.contrast = contrast;
	}
}