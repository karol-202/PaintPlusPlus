package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;

public interface ColorsManipulator<P extends ColorsManipulator.ColorsManipulatorParams>
{
	interface ColorsManipulatorParams { }
	
	Bitmap run(Bitmap in, P params);
}