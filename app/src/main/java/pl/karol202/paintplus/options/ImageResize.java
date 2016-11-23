package pl.karol202.paintplus.options;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import pl.karol202.paintplus.PaintView;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.math.Utils;

import static android.content.DialogInterface.*;

public class ImageResize extends Option implements OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener
{
	private final int MAX_SIZE = 2048 * 2048;

	private AlertDialog dialog;

	private EditText editWidth;
	private EditText editHeight;
	private EditText editX;
	private EditText editY;
	private ImageView imagePreview;
	private CheckBox checkKeepRatio;

	private Bitmap preview;
	private Canvas prevEdit;
	private int width; //Aktualne wymiary
	private int height;
	private int oldWidth; //Wymiary obrazu przed rozpoczęciem zmieniania wymiarów.
	private int oldHeight;
	private float ratio = -1;
	private boolean dontFireEvent;

	public ImageResize(Activity activity, PaintView paintView)
	{
		super(activity, paintView);
	}

	public void execute()
	{
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.dialog_resize_image, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setTitle(R.string.dialog_resize_image);
		dialogBuilder.setView(view);
		dialogBuilder.setPositiveButton(R.string.ok, this);
		dialogBuilder.setNegativeButton(R.string.cancel, this);

		Point currentSize = paintView.getSize();
		oldWidth = width = currentSize.x;
		oldHeight = height = currentSize.y;

		editWidth = (EditText) view.findViewById(R.id.edit_image_width);
		editWidth.setText(Integer.toString(width));
		editWidth.addTextChangedListener(this);

		editHeight = (EditText) view.findViewById(R.id.edit_image_height);
		editHeight.setText(Integer.toString(height));
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
			if(width * height > MAX_SIZE)
			{
				Toast.makeText(activity, R.string.message_too_big, Toast.LENGTH_LONG).show();
				return;
			}
			int x = parseInt(editX.getText().toString());
			int y = parseInt(editY.getText().toString());
			paintView.resize(x, y, width, height);
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

			if(ratio != -1)
			{
				if(newWidth != width)
				{
					newHeight = Math.round(newWidth / ratio);
					editHeight.setText(Integer.toString(newHeight));
				}
				else if(newHeight != height)
				{
					newWidth = Math.round(newHeight * ratio);
					editWidth.setText(Integer.toString(newWidth));
				}
			}

			width = newWidth;
			height = newHeight;

			dontFireEvent = false;
		}
		updatePreview();
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
		int right = Math.round(Math.max(oldWidth, width + offsetX));
		int bottom = Math.round(Math.max(oldHeight, height + offsetY));

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
		float newRight = Utils.map(offsetX + width, min, max, 0, previewSize);
		float newBottom = Utils.map(offsetY + height, min, max, 0, previewSize);
		RectF newR = new RectF(newLeft, newTop, newRight, newBottom);

		Paint paint = new Paint();

		paint.setColor(Color.argb(255, 255, 255, 141));
		prevEdit.drawRect(oldR, paint);

		paint.setColor(Color.argb(255, 30, 136, 229));
		prevEdit.drawRect(newR, paint);
	}

	private int parseInt(String text)
	{
		return text.equals("") || text.equals("-") ? 0 : Integer.parseInt(text);
	}
}