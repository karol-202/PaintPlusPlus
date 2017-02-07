package pl.karol202.paintplus.image.layer.mode;

import android.content.Context;
import android.renderscript.RenderScript;
import pl.karol202.paintplus.R;

import java.util.ArrayList;

public class LayerModes
{
	public static LayerMode MODE_STANDARD;
	public static LayerMode MODE_ADD;

	private static ArrayList<LayerMode> modes;
	private static RenderScript renderScript;
	
	public static void init(Context context)
	{
		renderScript = RenderScript.create(context);
		
		MODE_STANDARD = new LayerModeDefault(R.string.layer_mode_standard);
		MODE_ADD = new LayerModeSum();
		
		modes = new ArrayList<>();
		modes.add(MODE_STANDARD);
		modes.add(MODE_ADD);
	}
	
	public static void setAntialiasing(boolean antialiasing)
	{
		for(LayerMode mode : modes) mode.setAntialiasing(antialiasing);
	}
	
	public static ArrayList<LayerMode> getModes()
	{
		return modes;
	}

	public static RenderScript getRenderScript()
	{
		return renderScript;
	}
}