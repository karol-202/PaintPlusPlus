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
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.AppContext;
import pl.karol202.paintplus.image.Image;

public abstract class OptionFlip extends Option implements DialogInterface.OnClickListener
{
	private AlertDialog dialog;
	
	private RadioButton radioHorizontal;
	private RadioButton radioVertical;
	
	OptionFlip(AppContext context, Image image)
	{
		super(context, image);
	}
	
	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_flip, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setTitle(getTitle());
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		
		radioHorizontal = view.findViewById(R.id.radio_horizontal);
		radioVertical = view.findViewById(R.id.radio_vertical);
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	protected abstract int getTitle();
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		int direction;
		if(radioHorizontal.isChecked() && !radioVertical.isChecked()) direction = Image.FLIP_HORIZONTALLY;
		else if(!radioHorizontal.isChecked() && radioVertical.isChecked()) direction = Image.FLIP_VERTICALLY;
		else
		{
			getAppContext().createSnackbar(R.string.message_flip_direction, Toast.LENGTH_SHORT).show();
			return;
		}
		flip(direction);
		getImage().updateImage();
	}
	
	protected abstract void flip(int direction);
}