package pl.karol202.paintplus.recent;

import android.graphics.Bitmap;

public interface OnFileEditListener
{
	void onFileEdited(String path, Bitmap bitmap);
}