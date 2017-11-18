package pl.karol202.paintplus.file.explorer;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

public class FileExplorerFactory
{
	public static FileExplorer createFileExplorer(Activity activity)
	{
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return new AppFileExplorer(activity);
		Intent testIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		testIntent.addCategory(Intent.CATEGORY_OPENABLE);
		testIntent.setType("image/*");
		
		if(testIntent.resolveActivity(activity.getPackageManager()) != null) return new SAFFileExplorer(activity);
		else return new AppFileExplorer(activity);
	}
}