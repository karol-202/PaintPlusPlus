package pl.karol202.paintplus.color;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.firebase.crash.FirebaseCrash;
import com.kunzisoft.androidclearchroma.ChromaDialog;
import com.kunzisoft.androidclearchroma.IndicatorMode;
import com.kunzisoft.androidclearchroma.colormode.ColorMode;
import com.kunzisoft.androidclearchroma.listener.OnColorSelectedListener;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.settings.ActivitySettings;

public class ColorsSelect extends Fragment implements View.OnClickListener, OnColorSelectedListener, ColorsSet.OnColorsChangeListener
{
	private static final int TARGET_FIRST = 0;
	private static final int TARGET_SECOND = 1;
	
	private ActivityPaint activityPaint;
	private Image image;
	private ColorsSet colors;
	private SharedPreferences preferences;

	private View colorFirst;
	private View colorSecond;
	private ImageButton buttonSwap;
	private int target;
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		init(activity);
	}
	
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		init(context);
	}
	
	private void init(Context context)
	{
		if(!(context instanceof ActivityPaint))
			throw new RuntimeException("ColorsSelect fragment can only be attached to ActivityPaint.");
		activityPaint = (ActivityPaint) context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.colors, container, false);
		image = activityPaint.getImage();
		colors = image.getColorsSet();
		colors.setListener(this);
		
		colorFirst = view.findViewById(R.id.view_color_first);
		colorFirst.setOnClickListener(this);

		colorSecond = view.findViewById(R.id.view_color_second);
		colorSecond.setOnClickListener(this);

		buttonSwap = (ImageButton) view.findViewById(R.id.button_colors_swap);
		buttonSwap.setOnClickListener(this);

		updateColors();
		return view;
	}
	
	public void updateColors()
	{
		colorFirst.setBackgroundColor(colors.getFirstColor());
		colorSecond.setBackgroundColor(colors.getSecondColor());
		image.updateImage();
	}
	
	@Override
	public void onClick(View v)
	{
		if(v == buttonSwap) colors.revert();
		else if(v == colorFirst) pickColor(TARGET_FIRST);
		else if(v == colorSecond) pickColor(TARGET_SECOND);
		updateColors();
	}

	private void pickColor(int target)
	{
		this.target = target;
		
		@ColorInt int color = target == 0 ? colors.getFirstColor() : colors.getSecondColor();
		new ChromaDialog.Builder().colorMode(getColorMode())
								  .initialColor(color)
								  .indicatorMode(IndicatorMode.DECIMAL)
								  .setOnColorSelectedListener(this)
								  .create()
								  .show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "ColorPicker");
	}
	
	private ColorMode getColorMode()
	{
		String value = preferences.getString(ActivitySettings.KEY_COLOR_MODE, "RGB");
		switch(value)
		{
		case "RGB": return ColorMode.RGB;
		case "HSV": return ColorMode.HSV;
		case "HSL": return ColorMode.HSL;
		case "CMYK": return ColorMode.CMYK255;
		}
		FirebaseCrash.report(new RuntimeException("Unknown color mode: " + value));
		return ColorMode.RGB;
	}
	
	@Override
	public void onPositiveButtonClick(@ColorInt int color)
	{
		if(target == TARGET_FIRST) colors.setFirstColor(color);
		else if(target == TARGET_SECOND) colors.setSecondColor(color);
		updateColors();
	}
	
	@Override
	public void onNegativeButtonClick(@ColorInt int color) { }
	
	@Override
	public void onColorsChanged()
	{
		updateColors();
	}
}
