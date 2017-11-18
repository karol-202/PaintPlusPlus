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

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.GraphicsHelper;

import static android.content.DialogInterface.OnClickListener;

public class OptionFileNew extends Option implements OnClickListener
{
	private AlertDialog dialog;
	private EditText editX;
	private EditText editY;

	public OptionFileNew(Context context, Image image)
	{
		super(context, image);
	}

	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_new_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_new_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);

		editX = view.findViewById(R.id.edit_image_x);
		editX.setText(String.valueOf(image.getWidth()));

		editY = view.findViewById(R.id.edit_image_y);
		editY.setText(String.valueOf(image.getHeight()));

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
			Toast.makeText(context, R.string.message_invalid_bounds, Toast.LENGTH_LONG).show();
			return;
		}
		if(x > GraphicsHelper.getMaxTextureSize() ||
		   y > GraphicsHelper.getMaxTextureSize())
		{
			Toast.makeText(context, R.string.message_too_big, Toast.LENGTH_LONG).show();
			return;
		}
		image.newImage(x, y);
		image.centerView();
	}
	
	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}