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

package pl.karol202.paintplus.options;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.util.GraphicsHelper;

import static android.content.DialogInterface.OnClickListener;

public class OptionFileNew extends Option implements OnClickListener
{
	private class EditTextListener implements TextWatcher
	{
		private TextInputLayout inputLayout;
		
		EditTextListener(TextInputLayout inputLayout)
		{
			this.inputLayout = inputLayout;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) { }
		
		@Override
		public void afterTextChanged(Editable s)
		{
			int value = parseInt(s.toString());
			if(value <= 0)
				inputLayout.setError(getString(R.string.message_image_invalid_size));
			else if(value > GraphicsHelper.getMaxTextureSize())
				inputLayout.setError(getString(R.string.message_image_size_too_big));
			else
			{
				inputLayout.setError(null);
				inputLayout.setErrorEnabled(false);
			}
		}
		
		private String getString(int resource)
		{
			return getContext().getString(resource);
		}
	}
	
	private AlertDialog dialog;
	private TextInputLayout inputLayoutX;
	private TextInputLayout inputLayoutY;
	private EditText editX;
	private EditText editY;

	public OptionFileNew(AppContext context, Image image)
	{
		super(context, image);
	}
	
	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_new_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(R.string.dialog_new_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		
		inputLayoutX = view.findViewById(R.id.inputLayout_image_x);
		
		inputLayoutY = view.findViewById(R.id.inputLayout_image_y);

		editX = view.findViewById(R.id.edit_image_x);
		editX.setText(String.valueOf(getImage().getWidth()));
		editX.addTextChangedListener(new EditTextListener(inputLayoutX));

		editY = view.findViewById(R.id.edit_image_y);
		editY.setText(String.valueOf(getImage().getHeight()));
		editY.addTextChangedListener(new EditTextListener(inputLayoutY));

		dialog = dialogBuilder.create();
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		int x = parseInt(editX.getText().toString());
		int y = parseInt(editY.getText().toString());
		
		if(x == 0 || y == 0)
		{
			getAppContext().createSnackbar(R.string.message_invalid_bounds, Snackbar.LENGTH_SHORT).show();
			return;
		}
		if(x > GraphicsHelper.getMaxTextureSize() ||
		   y > GraphicsHelper.getMaxTextureSize())
		{
			getAppContext().createSnackbar(R.string.message_too_big, Snackbar.LENGTH_SHORT).show();
			return;
		}
		getImage().newImage(x, y);
		getImage().centerView();
	}
	
	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}