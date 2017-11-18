package pl.karol202.paintplus.file.explorer;

import android.app.Activity;
import android.content.Intent;

public class AppFileExplorer implements FileExplorer
{
	private Activity activity;
	
	AppFileExplorer(Activity activity)
	{
		this.activity = activity;
	}
	
	@Override
	public void openFile(int requestCode)
	{
		Intent intent = new Intent(activity, ActivityFileOpen.class);
		activity.startActivityForResult(intent, requestCode);
	}
	
	@Override
	public void saveFile(int requestCode)
	{
		Intent intent = new Intent(activity, ActivityFileSave.class);
		activity.startActivityForResult(intent, requestCode);
	}
}