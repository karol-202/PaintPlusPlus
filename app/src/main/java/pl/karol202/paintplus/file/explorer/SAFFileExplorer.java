package pl.karol202.paintplus.file.explorer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class SAFFileExplorer implements FileExplorer
{
	private Activity activity;
	
	SAFFileExplorer(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void openFile(int requestCode)
	{
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		activity.startActivityForResult(intent, requestCode);
	}
	
	@Override
	public void saveFile(int requestCode)
	{
		Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		activity.startActivityForResult(intent, requestCode);
	}
}