package pl.karol202.paintplus.file;

import android.content.Intent;
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
		Intent data = new Intent();
		data.putExtra("filePath", file.getAbsolutePath());
		data.putExtra("fileName", file.getName());
		setResult(RESULT_OK, data);
		finish();
		return true;
	}
}