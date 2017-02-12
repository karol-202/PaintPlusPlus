package pl.karol202.paintplus.options;

import android.content.Context;
import android.graphics.Bitmap;
import pl.karol202.paintplus.color.manipulators.ColorsInvert;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;

public class OptionColorsInvert extends Option
{
	public OptionColorsInvert(Context context, Image image)
	{
		super(context, image);
	}
	
	@Override
	public void execute()
	{
		Layer layer = image.getSelectedLayer();
		Bitmap bitmapIn = layer.getBitmap();
		
		ColorsInvert invert = new ColorsInvert();
		Bitmap bitmapOut = invert.run(bitmapIn, new ColorsInvert.InvertParams());
		layer.setBitmap(bitmapOut);
	}
}