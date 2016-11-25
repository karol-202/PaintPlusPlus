package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class ImageScale extends Option implements DialogInterface.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener
{
	private final int MAX_SIZE = 2048 * 2048;
	
	private AlertDialog dialog;
	
	private EditText editWidth;
	private EditText editHeight;
	private CheckBox checkKeepRatio;
	private CheckBox checkSmooth;
	
	private int width;
	private int height;
	private float ratio;
	private boolean dontFireEvent;
	
	public ImageScale(Context context, Image image)
	{
		super(context, image);
	}
	
	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_scale_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_scale_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);
		
		width = image.getWidth();
		height = image.getHeight();
		ratio = -1;
		
		editWidth = (EditText) view.findViewById(R.id.edit_image_width);
		editWidth.setText(String.valueOf(width));
		editWidth.addTextChangedListener(this);
		
		editHeight = (EditText) view.findViewById(R.id.edit_image_height);
		editHeight.setText(String.valueOf(height));
		editHeight.addTextChangedListener(this);
		
		checkKeepRatio = (CheckBox) view.findViewById(R.id.check_keep_ratio);
		checkKeepRatio.setOnCheckedChangeListener(this);
		
		checkSmooth = (CheckBox) view.findViewById(R.id.check_scaling_smooth);
		
		dialog = dialogBuilder.create();
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == BUTTON_POSITIVE)
		{
			if(which * height > MAX_SIZE)
			{
				Toast.makeText(context, R.string.message_too_big, Toast.LENGTH_LONG).show();
				return;
			}
			boolean smooth = checkSmooth.isChecked();
			image.scale(width, height, smooth);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	@Override
	public void afterTextChanged(Editable s)
	{
		if(!dontFireEvent)
		{
			dontFireEvent = true;
			
			int width = parseInt(editWidth.getText().toString());
			int height = parseInt(editHeight.getText().toString());
			
			changeBounds(width, height);
			
			dontFireEvent = false;
		}
	}
	
	private void changeBounds(int width, int height)
	{
		if(ratio != -1)
		{
			if(width != this.width)
			{
				height = Math.round(width / ratio);
				editHeight.setText(String.valueOf(height));
			}
			else if(height != this.height)
			{
				width = Math.round(height * ratio);
				editWidth.setText(String.valueOf(width));
			}
		}
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if(isChecked && (width == 0 || height == 0))
		{
			checkKeepRatio.setChecked(false);
			return;
		}
		if(isChecked) ratio = (float) width / height;
		else ratio = -1;
	}
	
	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}