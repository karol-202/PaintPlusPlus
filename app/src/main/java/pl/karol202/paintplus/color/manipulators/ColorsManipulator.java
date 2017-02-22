package pl.karol202.paintplus.color.manipulators;

import android.graphics.Bitmap;
import pl.karol202.paintplus.color.manipulators.params.ColorsManipulatorParams;

public interface ColorsManipulator<P extends ColorsManipulatorParams>
{
	Bitmap run(Bitmap in, P params);
}