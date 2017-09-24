package pl.karol202.paintplus.color.picker.numerical;

abstract class ColorMode implements OnColorChangeListener
{
	ColorPickerNumericalInterface pickerInterface;
	boolean useAlpha;
	
	ColorMode(ColorPickerNumericalInterface pickerInterface)
	{
		this.pickerInterface = pickerInterface;
		this.useAlpha = pickerInterface.isUsingAlpha();
		createChannels();
	}
	
	abstract void createChannels();
	
	abstract void updateChannels();
}