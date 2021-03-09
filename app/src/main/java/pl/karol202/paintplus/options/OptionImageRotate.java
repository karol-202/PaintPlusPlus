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
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import com.google.android.material.snackbar.Snackbar;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.history.action.ActionImageRotate;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.image.Image.RotationAmount;
import pl.karol202.paintplus.legacy.AppContextLegacy;
import pl.karol202.paintplus.legacy.OptionLegacy;

public class OptionImageRotate extends OptionLegacy
{
	private RadioGroup radioGroupAngle;

	public OptionImageRotate(AppContextLegacy context, Image image)
	{
		super(context, image);
	}

	@Override
	@SuppressLint("InflateParams")
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.dialog_rotate_image, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.action_rotate_image);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok, (dialog, which) -> rotate());
		builder.setNegativeButton(R.string.cancel, null);

		radioGroupAngle = view.findViewById(R.id.radioGroup_angle);

		builder.show();
	}

	private void rotate()
	{
		switch(radioGroupAngle.getCheckedRadioButtonId())
		{
		case R.id.radio_angle_90:
			rotate(RotationAmount.ANGLE_90);
			break;
		case R.id.radio_angle_180:
			rotate(RotationAmount.ANGLE_180);
			break;
		case R.id.radio_angle_270:
			rotate(RotationAmount.ANGLE_270);
			break;
		default: showErrorSnackbar();
		}
	}

	private void rotate(RotationAmount rotationAmount)
	{
		ActionImageRotate action = new ActionImageRotate(getImage());
		action.setRotationAmount(rotationAmount);

		getImage().rotate(rotationAmount);

		action.applyAction();
	}

	private void showErrorSnackbar()
	{
		getAppContext().createSnackbar(R.string.message_rotation_angle, Snackbar.LENGTH_SHORT).show();
	}
}
