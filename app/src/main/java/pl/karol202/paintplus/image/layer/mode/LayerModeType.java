package pl.karol202.paintplus.image.layer.mode;

import pl.karol202.paintplus.R;

public enum LayerModeType
{
	MODE_STANDARD(R.string.layer_mode_standard, 0, LayerModeDefault.class),
	MODE_SCREEN(R.string.layer_mode_screen, 1, LayerModeScreen.class),
	MODE_OVERLAY(R.string.layer_mode_overlay, 1, LayerModeOverlay.class),
	MODE_ADD(R.string.layer_mode_add, 2, LayerModeAdd.class),
	MODE_SUBTRACTION(R.string.layer_mode_subtraction, 2, LayerModeSubtraction.class),
	MODE_DIFFERENCE(R.string.layer_mode_difference, 2, LayerModeDifference.class),
	MODE_MULTIPLY(R.string.layer_mode_multiply, 2, LayerModeMultiply.class),
	MODE_LIGHTER(R.string.layer_mode_lighter, 3, LayerModeLighter.class),
	MODE_DARKER(R.string.layer_mode_darker, 3, LayerModeDarker.class);
	
	private static boolean antialiasing;
	
	private int name;
	private int category;
	private Class<? extends LayerMode> layerMode;
	
	LayerModeType(int name, int category, Class<? extends LayerMode> layerMode)
	{
		this.name = name;
		this.category = category;
		this.layerMode = layerMode;
	}
	
	public int getName()
	{
		return name;
	}
	
	public int getCategory()
	{
		return category;
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