package pl.karol202.paintplus.options;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import pl.karol202.paintplus.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.util.GLHelper;
import pl.karol202.paintplus.util.Utils;

import static android.content.DialogInterface.*;

public class OptionImageResize extends Option implements OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener
{
	private AlertDialog dialog;

	private EditText editWidth;
	private EditText editHeight;
	private EditText editX;
	private EditText editY;
	private ImageView imagePreview;
	private CheckBox checkKeepRatio;

	private Bitmap preview;
	private Canvas prevEdit;
	private int newWidth;
	private int newHeight;
	private int oldWidth;
	private int oldHeight;
	private float ratio;
	private boolean dontFireEvent;

	public OptionImageResize(Context context, Image image)
	{
		super(context, image);
	}

	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_resize_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setTitle(R.string.dialog_resize_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);
		
		oldWidth = newWidth = image.getWidth();
		oldHeight = newHeight = image.getHeight();
		ratio = -1;

		editWidth = (EditText) view.findViewById(R.id.edit_image_width);
		editWidth.setText(String.valueOf(newWidth));
		editWidth.addTextChangedListener(this);

		editHeight = (EditText) view.findViewById(R.id.edit_image_height);
		editHeight.setText(String.valueOf(newHeight));
		editHeight.addTextChangedListener(this);

		editX = (EditText) view.findViewById(R.id.edit_image_x);
		editX.setText("0");
		editX.addTextChangedListener(this);

		editY = (EditText) view.findViewById(R.id.edit_image_y);
		editY.setText("0");
		editY.addTextChangedListener(this);

		imagePreview = (ImageView) view.findViewById(R.id.image_resize_preview);

		checkKeepRatio = (CheckBox) view.findViewById(R.id.check_keep_ratio);
		checkKeepRatio.setOnCheckedChangeListener(this);

		dialog = dialogBuilder.create();
		dialog.setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialog)
			{
				createPreview();
			}
		});
		dialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if(which == BUTTON_POSITIVE)
		{
			if(newWidth > GLHelper.getMaxTextureSize() ||
			   newHeight > GLHelper.getMaxTextureSize())
			{
				Toast.makeText(context, R.string.message_too_big, Toast.LENGTH_LONG).show();
				return;
			}
			int x = parseInt(editX.getText().toString());
			int y = parseInt(editY.getText().toString());
			image.resize(x, y, newWidth, newHeight);
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

			int newWidth = parseInt(editWidth.getText().toString());
			int newHeight = parseInt(editHeight.getText().toString());

			changeBounds(newWidth, newHeight);

			dontFireEvent = false;
		}
		updatePreview();
	}

	private void changeBounds(int newWidth, int newHeight)
	{
		if(ratio != -1)
		{
			if(newWidth != this.newWidth)
			{
				newHeight = Math.round(newWidth / ratio);
				editHeight.setText(String.valueOf(newHeight));
			}
			else if(newHeight != this.newHeight)
			{
				newWidth = Math.round(newHeight * ratio);
				editWidth.setText(String.valueOf(newWidth));
			}
		}
		this.newWidth = newWidth;
		this.newHeight = newHeight;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if(isChecked && (newWidth == 0 || newHeight == 0))
		{
			checkKeepRatio.setChecked(false);
			return;
		}
		if(isChecked) ratio = (float) newWidth / newHeight;
		else ratio = -1;
	}

	private void createPreview()
	{
		int x = imagePreview.getWidth();
		int y = imagePreview.getHeight();
		preview = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
		imagePreview.setImageBitmap(preview);
		prevEdit = new Canvas(preview);
		updatePreview();
	}

	private void updatePreview()
	{
		preview.eraseColor(Color.argb(0, 0, 0, 0));

		int offsetX = parseInt(editX.getText().toString());
		int offsetY = parseInt(editY.getText().toString());

		int left = Math.min(0, offsetX);
		int top = Math.min(0, offsetY);
		int right = Math.round(Math.max(oldWidth, newWidth + offsetX));
		int bottom = Math.round(Math.max(oldHeight, newHeight + offsetY));

		int min = Math.min(left, top);
		int max = Math.max(right, bottom);
		int previewSize = Math.max(preview.getWidth(), preview.getHeight());

		float oldLeft = Utils.map(0, min, max, 0, previewSize);
		float oldTop = Utils.map(0, min, max, 0, previewSize);
		float oldRight = Utils.map(oldWidth, min, max, 0, previewSize);
		float oldBottom = Utils.map(oldHeight, min, max, 0, previewSize);
		RectF oldR = new RectF(oldLeft, oldTop, oldRight, oldBottom);

		float newLeft = Utils.map(offsetX, min, max, 0, previewSize);
		float newTop = Utils.map(offsetY, min, max, 0, previewSize);
		float newRight = Utils.map(offsetX + newWidth, min, max, 0, previewSize);
		float newBottom = Utils.map(offsetY + newHeight, min, max, 0, previewSize);
		RectF newR = new RectF(newLeft, newTop, newRight, newBottom);

		Paint paint = new Paint();

		paint.setColor(Color.argb(255, 255, 255, 141));
		prevEdit.drawRect(oldR, paint);

		paint.setColor(Color.argb(204, 27, 124, 209));
		prevEdit.drawRect(newR, paint);
	}

	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}