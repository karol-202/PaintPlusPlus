/*
 * Copyright 2017 karol-202
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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