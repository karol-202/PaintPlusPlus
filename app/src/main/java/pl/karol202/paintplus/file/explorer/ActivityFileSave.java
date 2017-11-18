package pl.karol202.paintplus.file.explorer;

import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.file.ImageLoader;

import java.io.File;

public class ActivityFileSave extends ActivityFileChoose implements View.OnClickListener
{
	private File file;
	
	private EditText editFileName;
	private Button buttonSave;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		String fileName = getIntent().getStringExtra("fileName");
		
		editFileName = findViewById(R.id.edit_file_name);
		editFileName.setText(fileName != null ? fileName : "");
		
		buttonSave = findViewById(R.id.button_save);
		buttonSave.setOnClickListener(this);
	}
	
	@Override
	public int getLayout()
	{
		return R.layout.activity_file_save;
	}
	
	@Override
	public boolean onFileSelected(File file)
	{
		if(!super.onFileSelected(file)) return false;
		editFileName.setText(file.getName());
		return true;
	}
	
	@Override
	public void onClick(View v)
	{
		String fileName = editFileName.getText().toString();
		if(fileName.isEmpty()) return;
		if(!hasProperFormat(fileName)) showErrorDialog();
		else
		{
			file = new File(getCurrentDirectory(), fileName);
			if(file.exists()) showReplaceDialog();
			else saveFile();
		}
	}
	
	private boolean hasProperFormat(String fileName)
	{
		String[] parts = fileName.split("\\.");
		String extension = parts[parts.length - 1];
		for(String filter : ImageLoader.SAVE_FORMATS)
			if(extension.equalsIgnoreCase(filter)) return true;
		return false;
	}
	
	private void showErrorDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.unsupported_file_format);
		builder.setPositiveButton(R.string.ok, null);
		builder.show();
	}
	
	private void showReplaceDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_replace_file);
		builder.setMessage(R.string.replace_file_text);
		builder.setPositiveButton(R.string.replace, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				saveFile();
			}
		});
		builder.setNegativeButton(R.string.cancel, null);
		builder.show();
	}
	
	private void saveFile()
	{
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(file));
		setResult(RESULT_OK, intent);
		finish();
	}
}