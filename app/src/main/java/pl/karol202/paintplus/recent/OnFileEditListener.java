package pl.karol202.paintplus.recent;

import android.graphics.Bitmap;
import android.net.Uri;

public interface OnFileEditListener
{
	void onFileEdited(Uri uri, Bitmap bitmap);
}