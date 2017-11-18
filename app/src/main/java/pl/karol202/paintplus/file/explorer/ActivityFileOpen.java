package pl.karol202.paintplus.file.explorer;

import android.content.Intent;
import android.net.Uri;
import pl.karol202.paintplus.R;

import java.io.File;

public class ActivityFileOpen extends ActivityFileChoose
{
	@Override
	public int getLayout()
	{
		return R.layout.activity_file_open;
	}
	
	@Override
	public boolean onFileSelected(File file)
	{
		if(!super.onFileSelected(file)) return false;
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(file));
		setResult(RESULT_OK, intent);
		finish();
		return true;
	}
}