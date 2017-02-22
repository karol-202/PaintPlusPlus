package pl.karol202.paintplus.options;

import android.content.Context;
import android.graphics.Bitmap;
import pl.karol202.paintplus.color.manipulators.ColorsInvert;
import pl.karol202.paintplus.color.manipulators.params.InvertParams;
import pl.karol202.paintplus.color.manipulators.params.ManipulatorSelection;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.layer.Layer;
import pl.karol202.paintplus.tool.selection.Selection;

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
		Selection selection = image.getSelection();
		
		ColorsInvert invert = new ColorsInvert();
		InvertParams params = new InvertParams(ManipulatorSelection.fromSelection(selection, layer.getBounds()));
		Bitmap bitmapOut = invert.run(bitmapIn, params);
		layer.setBitmap(bitmapOut);
	}
}