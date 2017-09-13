package pl.karol202.paintplus.image.layer.mode;

import pl.karol202.paintplus.R;

public enum LayerModeType
{
	MODE_STANDARD(R.string.layer_mode_standard, LayerModeDefault.class),
	MODE_ADD(R.string.layer_mode_add, LayerModeAdd.class),
	MODE_SUBTRACTION(R.string.layer_mode_subtraction, LayerModeSubtraction.class),
	MODE_DIFFERENCE(R.string.layer_mode_difference, LayerModeDifference.class),
	MODE_MULTIPLY(R.string.layer_mode_multiply, LayerModeMultiply.class);
	
	private static boolean antialiasing;
	
	private int name;
	private Class<? extends LayerMode> layerMode;
	
	LayerModeType(int name, Class<? extends LayerMode> layerMode)
	{
		this.name = name;
		this.layerMode = layerMode;
	}
	
	public int getName()
	{
		return name;
	}
	
	public Class<? extends LayerMode> getLayerModeClass()
	{
		return layerMode;
	}
	
	public static int getIndexOfType(LayerMode mode)
	{
		for(int i = 0; i < values().length; i++)
			if(mode.getClass() == values()[i].getLayerModeClass()) return i;
		return -1;
	}
	
	public static boolean isAntialiasing()
	{
		return antialiasing;
	}
	
	public static void setAntialiasing(boolean antialiasing)
	{
		LayerModeType.antialiasing = antialiasing;
	}
}