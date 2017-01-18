package pl.karol202.paintplus.color;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.colormode.ColorMode;
import pl.karol202.paintplus.image.Image;
import pl.karol202.paintplus.R;
import pl.karol202.paintplus.activity.ActivityPaint;

public class ColorsSelect extends Fragment implements View.OnClickListener, com.pavelsikun.vintagechroma.OnColorSelectedListener
{
	private static final int TARGET_FIRST = 0;
	private static final int TARGET_SECOND = 1;
	
	private ActivityPaint activityPaint;
	private Image image;
	private ColorsSet colors;

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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.colors, container, false);
		image = activityPaint.getImage();
		colors = image.getColorsSet();
		
		colorFirst = view.findViewById(R.id.view_color_first);
		colorFirst.setOnClickListener(this);

		colorSecond = view.findViewById(R.id.view_color_second);
		colorSecond.setOnClickListener(this);

		buttonSwap = (ImageButton) view.findViewById(R.id.button_colors_swap);
		buttonSwap.setOnClickListener(this);

		updateColors();
		return view;
	}

	@Override
	public void onClick(View v)
	{
		if(v == buttonSwap) colors.revert();
		else if(v == colorFirst) pickColor(TARGET_FIRST);
		else if(v == colorSecond) pickColor(TARGET_SECOND);
		updateColors();
	}

	public void updateColors()
	{
		colorFirst.setBackgroundColor(colors.getFirstColor());
		colorSecond.setBackgroundColor(colors.getSecondColor());
		image.updateImage();
	}

	private void pickColor(int target)
	{
		this.target = target;
		@ColorInt int color = target == 0 ? colors.getFirstColor() : colors.getSecondColor();
		new ChromaDialog.Builder().colorMode(ColorMode.RGB)
								  .initialColor(color)
								  .indicatorMode(IndicatorMode.DECIMAL)
								  .onColorSelected(this)
								  .create()
								  .show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "ColorPicker");
	}

	@Override
	public void onColorSelected(@ColorInt int color)
	{
		if(target == TARGET_FIRST) colors.setFirstColor(color);
		else if(target == TARGET_SECOND) colors.setSecondColor(color);
		updateColors();
	}
}
